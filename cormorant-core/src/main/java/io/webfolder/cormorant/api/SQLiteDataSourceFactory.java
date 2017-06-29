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
