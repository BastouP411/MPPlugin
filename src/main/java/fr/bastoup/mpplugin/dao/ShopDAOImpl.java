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
import fr.bastoup.mpplugin.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.bastoup.mpplugin.dao.DAOUtils.close;
import static fr.bastoup.mpplugin.dao.DAOUtils.preparedStatementInit;

public class ShopDAOImpl implements ShopDAO {

    private static final String SQL_SELECT = "SELECT * FROM shops WHERE id = ?;";
    private static final String SQL_SELECT_COORDS = "SELECT * FROM shops WHERE x = ? AND y = ? AND z = ?;";
    private static final String SQL_SELECT_UUID = "SELECT * FROM shops WHERE uuid = ?;";
    private static final String SQL_SELECT_UUID_NAME = "SELECT * FROM shops WHERE uuid = ? AND LOWER(name) = LOWER(?);";
    private static final String SQL_INSERT = "INSERT INTO shops (owner, name, bank, price, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String SQL_UPDATE = "UPDATE shops SET owner = ?, name = ?, bank = ?, price = ?, x = ?, y = ?, z = ? WHERE id = ?;";
    private static final String SQL_DELETE = "DELETE FROM shops WHERE id = ?;";

    private DAOFactory factory;

    public ShopDAOImpl(DAOFactory factory) {
        this.factory = factory;
    }

    @Override
    public void create(Shop shop) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet autogenValues = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_INSERT, true,
                    shop.getOwner().toString(),
                    shop.getName(),
                    shop.isBank(),
                    shop.getPrice(),
                    shop.getPrice(),
                    shop.getX(),
                    shop.getY(),
                    shop.getZ()
            );
            int statut = preparedStatement.executeUpdate();
            if ( statut == 0 ) {
                throw new DAOException( "No Shop created." );
            }

            autogenValues = preparedStatement.getGeneratedKeys();

            if(autogenValues.next()) {
                shop.setId(autogenValues.getLong(1));
            } else {
                throw new DAOException( "No Shop created." );
            }

        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( autogenValues, preparedStatement, con );
        }
    }

    @Override
    public void update(Shop shop) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_UPDATE, false,
                    shop.getOwner().toString(),
                    shop.getName(),
                    shop.isBank(),
                    shop.getPrice(),
                    shop.getPrice(),
                    shop.getX(),
                    shop.getY(),
                    shop.getZ(),
                    shop.getId()
            );
            int statut = preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( preparedStatement, con );
        }
    }

    @Override
    public void delete(Shop shop) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_DELETE, false, shop.getId());
            int statut = preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( preparedStatement, con );
        }
    }

    @Override
    public List<Shop> getUserShops(UUID uuid) {
        return getUserShops(uuid.toString());
    }

    @Override
    public List<Shop> getUserShops(String uuid) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Shop> shops = new ArrayList<>();

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT_UUID, false, uuid );
            resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {
                shops.add(map( resultSet ));
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return shops;
    }

    @Override
    public Shop getUserShop(UUID uuid, String name) {
        return getUserShop(uuid.toString(), name);
    }

    @Override
    public Shop getUserShop(String uuid, String name) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Shop shop = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT_UUID_NAME, false, uuid, name );
            resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                shop = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return shop;
    }

    @Override
    public Shop get(long id) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Shop shop = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT, false, id );
            resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                shop = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return shop;
    }

    @Override
    public Shop get(int x, int y, int z) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Shop shop = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT_COORDS, false, x, y, z );
            resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                shop = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return shop;
    }

    private Shop map(ResultSet resultSet) throws SQLException {
        return new Shop(
                resultSet.getLong("id"),
                resultSet.getString("uuid"),
                resultSet.getString("name"),
                resultSet.getBoolean("bank"),
                resultSet.getInt("price"),
                resultSet.getInt("x"),
                resultSet.getInt("y"),
                resultSet.getInt("z")
        );
    }
}
