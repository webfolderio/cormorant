package io.webfolder.cormorant.api;

import static org.sqlite.SQLiteConfig.Encoding.UTF8;

import javax.sql.DataSource;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class SQLiteDataSourceFactory implements DataSourceFactory {

    private final SQLiteDataSource ds;

    public SQLiteDataSourceFactory() {
        SQLiteConfig config = new SQLiteConfig();
        config.setEncoding(UTF8);
        config.setSharedCache(true);
        ds = new SQLiteDataSource(config);
        ds.setUrl("jdbc:sqlite:cormorant.db");
    }

    @Override
    public DataSource get() {
        return ds;
    }
}
