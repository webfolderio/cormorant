/**
 * The MIT License
 * Copyright © 2017, 2019 WebFolder OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.webfolder.cormorant.api.metadata;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.webfolder.cormorant.api.service.MetadataService;

public class JdbcMetadaService implements MetadataService {

    private final DataSource ds;

    private final String     schema;

    private final String     table;

    private final Set<String> DECODES = unmodifiableSet(new HashSet<>(singletonList("X-Object-Manifest")));

    private static final Logger LOG   = LoggerFactory.getLogger(JdbcMetadaService.class);

    public JdbcMetadaService(
                final DataSource ds,
                final String     schema,
                final String     table) {
        this.ds         = ds;
        this.schema     = schema;
        this.table      = table;
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
        if (value == null) {
            if (contains(namespace, key)) {
                delete(namespace, key);
            }
            return;
        }
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
        if (value == null) {
            return;
        }
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

    @Override
    public void init() throws SQLException {
        final String tableDDL = "create table "        +
                                    getSchemaKeyword() +
                                    table              +
                                    " (NAMESPACE VARCHAR(1024), KEY VARCHAR(1024), VALUE VARCHAR(4096))";
        final String idxDDL   = "create index IDX_" + table + " on " + getSchemaKeyword() + table + "(NAMESPACE, KEY)";
        LOG.info("Executing DDL: " + tableDDL);
        LOG.info("Executing DDL: " + idxDDL  );
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
        }
    }
}
