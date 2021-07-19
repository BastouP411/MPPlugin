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

import java.sql.*;

public class DAOUtils {
    public static PreparedStatement preparedStatementInit(Connection con, String sql, boolean returnGeneratedKeys,
                                                          Object... obj) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement(sql,
                returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        for (int i = 0; i < obj.length; i++) {
            preparedStatement.setObject(i + 1, obj[i]);
        }
        return preparedStatement;
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("Couldn't close ResultSet : " + e.getMessage());
            }
        }
    }

    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Couldn't close Statement : " + e.getMessage());
            }
        }
    }

    public static void close(Connection connexion) {
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException e) {
                System.out.println("Couldn't close Connection : " + e.getMessage());
            }
        }
    }

    public static void close(Statement statement, Connection connexion) {
        close(statement);
        close(connexion);
    }

    public static void close(ResultSet resultSet, Statement statement, Connection connexion) {
        close(resultSet);
        close(statement);
        close(connexion);
    }

    public static Long getNullableLong(ResultSet set, String col) throws SQLException {
        long res = set.getLong(col);
        return set.wasNull() ? null : res;
    }
}
