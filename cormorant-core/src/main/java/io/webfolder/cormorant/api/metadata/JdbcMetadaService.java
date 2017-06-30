/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (support@webfolder.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.webfolder.cormorant.api.metadata;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.MetadataService;

public class JdbcMetadaService implements MetadataService {

    private final DataSource ds;

    private final String     schema;

    private final String     table;

    private final Set<String> DECODES = unmodifiableSet(new HashSet<>(asList("X-Object-Manifest")));

    private static final Logger LOG   = LoggerFactory.getLogger(JdbcMetadaService.class);

    public JdbcMetadaService(
                final DataSource ds,
                final String     schema,
                final String     table) {
        this.ds         = ds;
        this.schema     = schema;
        this.table      = table;
        init();
    }

    @Override
    public String get(final String namespace, final String key) throws SQLException {
        final String sql = "select VALUE from " + getSchemaKeyword() + table + " where NAMESPACE = ? and KEY = ?";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namespace);
            pstmt.setString(2, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("VALUE");
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public boolean contains(final String namespace, final String key) throws SQLException {
        return get(namespace, key) != null;
    }

    @Override
    public void update(final String namespace, final String key, final String value) throws SQLException {
        final String sql = "update " + getSchemaKeyword() + table + " set VALUE = ? where NAMESPACE = ? and KEY = ?";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String data = value;
            if (DECODES.contains(key)) {
                data = data.replace("%2F", "/");
            }
            pstmt.setObject(1, data);
            pstmt.setString(2, namespace);
            pstmt.setString(3, key);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void add(final String namespace, final String key, final String value) throws SQLException {
        final String sql = "insert into " + getSchemaKeyword() + table + " (NAMESPACE, KEY, VALUE) VALUES (?, ?, ?)";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String data = value;
            if (DECODES.contains(key)) {
                data = data.replace("%2F", "/");
            }
            pstmt.setString(1, namespace);
            pstmt.setString(2, key);
            pstmt.setObject(3, data);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Map<String, Object> getValues(final String namespace) throws SQLException {
        final Map<String, Object> values = new HashMap<>();
        final String sql = "select KEY, VALUE from " + getSchemaKeyword() + table + " where NAMESPACE = ?";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namespace);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    final String key   = rs.getString("KEY");
                    final Object value = rs.getObject("VALUE");
                    values.put(key, value);
                }
            }
        }
        return values;
    }

    @Override
    public void setValues(final String namespace, final Map<String, Object> values) throws SQLException {
        for (Entry<String, Object> next : values.entrySet()) {
            final String key   = next.getKey();
            final Object value = next.getValue();
            if (get(namespace, key) == null) {
                add(namespace, key, value != null ? valueOf(value) : null);
            } else {
                update(namespace, key, value != null ? valueOf(value) : null);
            }
        }
    }

    @Override
    public void delete(final String namespace, final String key) throws SQLException {
        final String sql = "delete from " + getSchemaKeyword() + table + " where NAMESPACE = ? and KEY = ?";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namespace);
            pstmt.setString(2, key);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(final String namespace) throws SQLException {
        final String sql = "delete from " + getSchemaKeyword() + table + " where NAMESPACE = ?";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namespace);
            pstmt.executeUpdate();
        }
    }

    protected String getSchemaKeyword() {
        return schema.isEmpty() ? "" : schema + ".";
    }

    protected void init() {
        final String tableDDL = "create table "        +
                                    getSchemaKeyword() +
                                    table              +
                                    " (NAMESPACE VARCHAR(1024), KEY VARCHAR(1024), VALUE VARCHAR(4096))";
        final String idxDDL   = "create index IDX_" + table + " on " + getSchemaKeyword() + table + "(NAMESPACE, KEY)";
        LOG.error("Executing DDL: " + tableDDL);
        LOG.error("Executing DDL: " + idxDDL  );
        try (Connection conn = ds.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, schema.isEmpty() ? null : schema, table, new String[] { "TABLE" });
            if ( ! rs.next() ) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(tableDDL);
                    LOG.info("Database table [{}] created.", new Object[] { getSchemaKeyword() + table });
                    stmt.execute(idxDDL);
                    LOG.info("Table index [{}] created.", new Object[] { "IDX_" + table });
                }
            }
        } catch (SQLException e) {
            throw new CormorantException(e);
        }
    }
}
