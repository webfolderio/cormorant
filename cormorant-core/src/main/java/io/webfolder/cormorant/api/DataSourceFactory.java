package io.webfolder.cormorant.api;

import javax.sql.DataSource;

public interface DataSourceFactory {

    DataSource get();
}
