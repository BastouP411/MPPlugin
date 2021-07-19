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

package fr.bastoup.mpplugin.commands;

import fr.bastoup.mpplugin.MPPlugin;
import fr.bastoup.mpplugin.beans.User;
import fr.bastoup.mpplugin.handlers.UserHandler;
import fr.bastoup.mpplugin.util.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor {

    private final MPPlugin plugin;

    public HomeCommand(MPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Vous n'\u00EAtes pas un joueur. Vous ne pouvez pas utiliser cette commande.");
            return true;
        }

        UserHandler users = plugin.getUserHandler();
        User usr = users.getOrCreateUser(player.getUniqueId());

        if(args.length >= 1 && args[0].equalsIgnoreCase("set")) {
            Location loc = player.getLocation();
            users.setHome(usr, loc);
            player.sendMessage(ChatColor.DARK_AQUA + "[TELEPORT] " + ChatColor.AQUA + "Vous avez d\u00e9fini votre maison.");
        } else {

            if(usr.getHomeX() == null || usr.getHomeY() == null || usr.getHomeZ() == null || usr.getHomeWorld() == null) {
                player.sendMessage(ChatColor.RED + "Vous n'avez pas un d\u00e9fini de maison.");
                return true;
            }

            Location loc = new Location(Bukkit.getWorld(usr.getHomeWorld()), usr.getHomeX(), usr.getHomeY(), usr.getHomeZ());
            new Teleporter(player, loc, 5)
                    .runTaskTimer(plugin, 0, 20);
        }

        return true;

    }

    public static class TabCompleter implements org.bukkit.command.TabCompleter {

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> res = new ArrayList<>();
            if(args.length <= 1) {
                res.add("set");
            }

            return res;
        }
    }
}
