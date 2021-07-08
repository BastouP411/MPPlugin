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

import fr.bastoup.mpplugin.commands.BankCommand;
import fr.bastoup.mpplugin.commands.MoneyCommand;
import fr.bastoup.mpplugin.commands.ShopCommand;
import fr.bastoup.mpplugin.dao.DAOFactory;
import fr.bastoup.mpplugin.events.BankEvents;
import fr.bastoup.mpplugin.events.ShopEvents;
import fr.bastoup.mpplugin.handlers.ShopHandler;
import fr.bastoup.mpplugin.handlers.UserHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MPPlugin extends JavaPlugin {

    private DAOFactory daoFactory = null;
    private UserHandler userHandler = null;
    private ShopHandler shopHandler = null;

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
        this.shopHandler = new ShopHandler(this);

        registerCommands();
        registerEvents();
    }

    public DAOFactory getDAOFactory() {
        return daoFactory;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public ShopHandler getShopHandler() {
        return shopHandler;
    }

    public void registerCommands() {
        this.getCommand("money").setExecutor(new MoneyCommand(this));
        this.getCommand("money").setTabCompleter(new MoneyCommand.TabCompleter());

        this.getCommand("shop").setExecutor(new ShopCommand(this));
        this.getCommand("shop").setTabCompleter(new ShopCommand.TabCompleter());

        this.getCommand("bank").setExecutor(new BankCommand(this));
        this.getCommand("bank").setTabCompleter(new BankCommand.TabCompleter());
    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ShopEvents(this), this);
        pm.registerEvents(new BankEvents(this), this);
    }
}
