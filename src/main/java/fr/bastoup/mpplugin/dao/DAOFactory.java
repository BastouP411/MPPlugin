/*
 * Copyright 2021 BastouP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package fr.bastoup.mpplugin.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.bastoup.mpplugin.MPPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static fr.bastoup.mpplugin.dao.DAOUtils.*;

public class DAOFactory {

    private final HikariDataSource pool;

    private static final String USERS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS `users` ( `uuid` varchar(60) NOT NULL, `money` bigint(20) NOT NULL, PRIMARY KEY (`uuid`), UNIQUE KEY `uuid_UNIQUE` (`uuid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    private static final String SHOPS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS `shops` ( `id` BIGINT(20) NOT NULL AUTO_INCREMENT, `owner` VARCHAR(60), `name` VARCHAR(15) NOT NULL, `bank` TINYINT(1) NOT NULL, `price` INT NOT NULL, `x` INT NOT NULL, `y` INT NOT NULL, `z` INT NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `id_UNIQUE` (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    public static DAOFactory getInstance(MPPlugin mpPlugin) throws DAOConfigurationException {
        FileConfiguration conf = mpPlugin.getConfig();

        String host = conf.getString("database.host");
        int port = conf.getInt("database.port");
        String db = conf.getString("database.database");
        String username = conf.getString("database.username");
        String password = conf.getString("database.password");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new DAOConfigurationException("Le driver est introuvable dans le classpath.", e);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        HikariDataSource pool = new HikariDataSource(hikariConfig);

        DAOFactory instance = new DAOFactory(pool);
        return instance;
    }

    DAOFactory(HikariDataSource pool) {
        this.pool = pool;
    }

    public void setupDB(){
        Connection con = null;
        Statement stmt = null;
        try {
            con = getConnection();
            stmt = con.createStatement();

            stmt.executeUpdate(USERS_TABLE_CREATE);
            stmt.executeUpdate(SHOPS_TABLE_CREATE);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(stmt, con);
        }
    }

    Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    public void closeFactory() {
        pool.close();
    }

    public UserDAO getUserDAO() {
        return new UserDAOImpl(this);
    }

    public ShopDAO getShopDAO() {
        return new ShopDAOImpl(this);
    }
}
