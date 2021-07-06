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

import fr.bastoup.mpplugin.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static fr.bastoup.mpplugin.dao.DAOUtils.*;

public class UserDAOImpl implements UserDAO {

    private static final String SQL_SELECT_UUID = "SELECT * FROM users WHERE uuid = ?";
    private static final String SQL_INSERT = "INSERT INTO users (uuid, money) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE users SET money=? WHERE uuid=?;";

    private DAOFactory factory;

    public UserDAOImpl(DAOFactory factory) {
        this.factory = factory;
    }

    @Override
    public void create(User user) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_INSERT, false, user.getUUID().toString(), user.getMoney());
            int statut = preparedStatement.executeUpdate();
            if ( statut == 0 ) {
                throw new DAOException( "No User created." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( preparedStatement, con );
        }
    }

    @Override
    public void update(User user) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_UPDATE, false, user.getMoney(), user.getUUID().toString() );
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( preparedStatement, con );
        }
    }

    @Override
    public User get(String uuid) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            con = factory.getConnection();
            preparedStatement = preparedStatementInit( con, SQL_SELECT_UUID, false, uuid );
            resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                user = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            close( resultSet, preparedStatement, con );
        }

        return user;
    }

    public User get(UUID uuid) {
        return get(uuid.toString());
    }

    private User map(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("uuid"),
                resultSet.getInt("money")
        );
    }
}
