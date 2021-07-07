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

package fr.bastoup.mpplugin;

import fr.bastoup.mpplugin.commands.MoneyCommand;
import fr.bastoup.mpplugin.dao.DAOFactory;
import fr.bastoup.mpplugin.handlers.UserHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class MPPlugin extends JavaPlugin {

    private DAOFactory daoFactory = null;
    private UserHandler userHandler = null;

    @Override
    public void onDisable() {
        if(this.daoFactory != null) {
            this.daoFactory.closeFactory();
            this.daoFactory = null;
        }
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.daoFactory = DAOFactory.getInstance(this);
        this.daoFactory.setupDB();

        this.userHandler = new UserHandler(this);

        registerCommands();
        registerEvents();
    }

    public DAOFactory getDAOFactory() {
        return daoFactory;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public void registerCommands() {
        this.getCommand("money").setExecutor(new MoneyCommand(this));
        this.getCommand("money").setTabCompleter(new MoneyCommand.TabCompleter());
    }

    public void registerEvents() {

    }
}
