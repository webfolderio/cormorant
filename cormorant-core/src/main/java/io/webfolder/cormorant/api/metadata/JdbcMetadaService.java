package io.webfolder.cormorant.api.metadata;

import static java.lang.String.valueOf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
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

    private static final Logger LOG = LoggerFactory.getLogger(JdbcMetadaService.class);

    public JdbcMetadaService(
                final DataSource ds,
                final String     schema,
                final String     table) {
        this.ds         = ds;
        this.schema     = schema;
        this.table      = table;
        createTable();
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
        final String sql = "select KEY from " + getSchemaKeyword() + table + " where NAMESPACE = ? and KEY = ?";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namespace);
            pstmt.setString(2, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void update(final String namespace, final String key, final String value) throws SQLException {
        final String sql = "update " + getSchemaKeyword() + table + " set VALUE = ? where NAMESPACE = ? and KEY = ?";
        try (Connection conn = ds.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, value);
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
            pstmt.setString(1, namespace);
            pstmt.setString(2, key);
            pstmt.setObject(3, value);
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

    protected void createTable() {
        try (Connection conn = ds.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, schema.isEmpty() ? null : schema, table, new String[] { "TABLE" });
            if ( ! rs.next() ) {
                try (Statement stmt = conn.createStatement()) {
                    final String ddl = "create table " +
                                    table + " (NAMESPACE VARCHAR(1024), KEY VARCHAR(1024), VALUE VARCHAR(4096))";
                    stmt.execute(ddl);
                    LOG.info("SQLite table [{}] created.", new Object[] { table });
                }
            }
        } catch (SQLException e) {
            throw new CormorantException(e);
        }
    }
}
