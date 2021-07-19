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

import fr.bastoup.mpplugin.beans.Shop;
import fr.bastoup.mpplugin.beans.ShopManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.bastoup.mpplugin.dao.DAOUtils.close;
import static fr.bastoup.mpplugin.dao.DAOUtils.preparedStatementInit;

public class ShopManagerDAOImpl implements ShopManagerDAO{

    private static final String SQL_SELECT = "SELECT * FROM managers WHERE id = ?;";
    private static final String SQL_SELECT_USER = "SELECT * FROM managers WHERE user = ?;";
    private static final String SQL_SELECT_SHOP = "SELECT * FROM managers WHERE shop = ?;";
    private static final String SQL_INSERT = "INSERT INTO managers (user, shop) VALUES (?, ?);";
    private static final String SQL_UPDATE = "UPDATE managers SET user = ?, shop = ? WHERE id = ?;";
    private static final String SQL_DELETE = "DELETE FROM managers WHERE id = ?;";
    private static final String SQL_DELETE_SHOP = "DELETE FROM managers WHERE shop = ?;";

    private DAOFactory factory;

    public ShopManagerDAOImpl(DAOFactory factory) {
        this.factory = factory;
    }

    @Override
    public void create(ShopManager manager) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet autogenValues = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_INSERT, true,
                    manager.getUser().toString(),
                    manager.getShop()
            );
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "No Manager created." );
            }

            autogenValues = preparedStatement.getGeneratedKeys();

            if(autogenValues.next()) {
                manager.setId(autogenValues.getLong(1));
            } else {
                throw new DAOException( "No Manager created." );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( autogenValues, preparedStatement, con );
        }
    }

    @Override
    public void update(ShopManager manager) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_UPDATE, false,
                    manager.getUser().toString(),
                    manager.getShop(),
                    manager.getId()
            );
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( preparedStatement, con );
        }
    }

    @Override
    public void delete(ShopManager manager) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_DELETE, false, manager.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( preparedStatement, con );
        }
    }

    @Override
    public void deleteShop(long shop) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_DELETE_SHOP, false, shop);
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( preparedStatement, con );
        }
    }

    @Override
    public List<ShopManager> getManaged(UUID uuid) {
        return getManaged(uuid.toString());
    }

    @Override
    public List<ShopManager> getManaged(String uuid) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<ShopManager> managers = new ArrayList<>();

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT_USER, false, uuid );
            resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {
                managers.add(map( resultSet ));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return managers;
    }

    @Override
    public List<ShopManager> getManagers(long shop) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<ShopManager> managers = new ArrayList<>();

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT_SHOP, false, shop );
            resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {
                managers.add(map( resultSet ));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return managers;
    }

    @Override
    public ShopManager get(long id) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ShopManager manager = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT, false, id );
            resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                manager = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return manager;
    }

    private ShopManager map(ResultSet resultSet) throws SQLException {
        return new ShopManager(
                resultSet.getLong("id"),
                resultSet.getLong("shop"),
                resultSet.getString("user")
        );
    }
}
