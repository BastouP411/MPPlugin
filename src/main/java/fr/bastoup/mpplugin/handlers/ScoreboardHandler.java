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
import fr.bastoup.mpplugin.beans.Shop;
import fr.bastoup.mpplugin.beans.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Collection;
import java.util.List;

public class ScoreboardHandler {
    private MPPlugin plugin;

    public ScoreboardHandler(MPPlugin plugin) {
        this.plugin = plugin;
    }

    public void createScoreboard(Player player) {
        String currency = plugin.getConfig().getString("currency");
        User usr = plugin.getUserHandler().getOrCreateUser(player.getUniqueId());
        List<Shop> shops = plugin.getShopHandler().getShops(player.getUniqueId());

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("mp", "dummy", ChatColor.DARK_AQUA + "MP*1");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team moneyCounter = board.registerNewTeam("moneyCounter");
        moneyCounter.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE);

        Team shopCounter = board.registerNewTeam("shopCounter");
        shopCounter.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE);

        Score moneyTitle = obj.getScore(ChatColor.AQUA + "\u00bb Argent");
        moneyTitle.setScore(-1);
        obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE).setScore(-2);
        moneyCounter.setPrefix(ChatColor.GREEN + "" + usr.getMoney() + " " + currency);

        Score blank = obj.getScore(ChatColor.DARK_BLUE + "" + ChatColor.WHITE);
        blank.setScore(-3);

        Score shopTitle = obj.getScore(ChatColor.AQUA + "\u00bb Shops");
        shopTitle.setScore(-4);
        obj.getScore(ChatColor.BLUE + "" + ChatColor.WHITE).setScore(-5);
        shopCounter.setPrefix(ChatColor.GREEN + "" + shops.size() + " shop(s)");

        player.setScoreboard(board);
    }

    public void updateScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        Team moneyCounter = board.getTeam("moneyCounter");
        Team shopCounter = board.getTeam("shopCounter");
        if(moneyCounter == null || shopCounter == null) {
            createScoreboard(player);
            return;
        }

        String currency = plugin.getConfig().getString("currency");
        User usr = plugin.getUserHandler().getOrCreateUser(player.getUniqueId());
        List<Shop> shops = plugin.getShopHandler().getShops(player.getUniqueId());

        moneyCounter.setPrefix(ChatColor.GREEN + "" + usr.getMoney() + " " + currency);
        shopCounter.setPrefix(ChatColor.GREEN + "" + shops.size() + " shop(s)");

    }

    public static class UpdaterTask implements Runnable {

        private MPPlugin plugin;

        public UpdaterTask(MPPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for(Player pl : players) {
                plugin.getScoreboardHandler().updateScoreboard(pl);
            }
        }
    }
}
