package io.webfolder.cormorant.api;

import javax.sql.DataSource;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.SQLiteConfig.Encoding;

public class SQLiteDataSourceFactory implements DataSourceFactory {

    private final SQLiteDataSource ds;

    public SQLiteDataSourceFactory() {
        SQLiteConfig config = new SQLiteConfig();
        config.setEncoding(Encoding.UTF8);
        ds = new SQLiteDataSource(config);
        ds.setUrl("jdbc:sqlite:cormorant.db");
    }

    @Override
    public DataSource get() {
        return ds;
    }
}
