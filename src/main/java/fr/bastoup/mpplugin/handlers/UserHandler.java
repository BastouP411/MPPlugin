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

package fr.bastoup.mpplugin.handlers;

import fr.bastoup.mpplugin.MPPlugin;
import fr.bastoup.mpplugin.beans.User;
import fr.bastoup.mpplugin.dao.UserDAO;

import java.util.UUID;

public class UserHandler {

    private final MPPlugin plugin;

    public UserHandler(MPPlugin plugin) {
        this.plugin = plugin;
    }

    public User getUser(UUID uuid) {
        return this.plugin.getDAOFactory().getUserDAO().get(uuid);
    }

    public User getUser(String uuid) {
        return this.plugin.getDAOFactory().getUserDAO().get(uuid);
    }

    public User getOrCreateUser(UUID uuid) {
        return getOrCreateUser(uuid.toString());
    }

    public User getOrCreateUser(String uuid) {
        UserDAO usrDao = this.plugin.getDAOFactory().getUserDAO();
        User usr = usrDao.get(uuid);
        if(usr == null) {
            usr = new User(uuid, plugin.getConfig().getInt("startingMoney"));
            usrDao.create(usr);
        }
        return usr;
    }

    public void addMoney(User usr, int money) throws HandlersException {
        if(usr.getMoney() + money < 0) {
            throw new HandlersException("The user has not enough money");
        }

        usr.setMoney(usr.getMoney() + money);
        this.plugin.getDAOFactory().getUserDAO().update(usr);
    }

    public void removeMoney(User usr, int money) throws HandlersException {
        if(usr.getMoney() - money < 0) {
            throw new HandlersException("The user has not enough money");
        }

        usr.setMoney(usr.getMoney() - money);
        this.plugin.getDAOFactory().getUserDAO().update(usr);
    }

    public void transferMoney(User from, User to, int money) throws HandlersException {
        if(from.getUUID().equals(to.getUUID()))
            return;
        removeMoney(from, money);
        addMoney(to, money);
    }
}
