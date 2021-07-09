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
import fr.bastoup.mpplugin.handlers.HandlersException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoneyCommand  implements CommandExecutor {

    private final MPPlugin plugin;

    public MoneyCommand(MPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Vous n'\u00EAtes pas un joueur. Vous ne pouvez pas utiliser cette commande.");
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Vous devez fournir au moins un argument.");
            sender.sendMessage(ChatColor.RED + "Utilisation: /money <balance|transfer> [joueur] [montant]");
            return true;
        }

        String currency = plugin.getConfig().getString("currency");
        User usr = plugin.getUserHandler().getOrCreateUser(player.getUniqueId());
        if(args[0].equalsIgnoreCase("balance")) {
            player.sendMessage(ChatColor.DARK_AQUA + "[BANQUE] " + ChatColor.AQUA +  "Vous poss\u00E9dez: " + ChatColor.GREEN + usr.getMoney() + " " + currency);
        } else if(args[0].equalsIgnoreCase("transfer")) {
            if(args.length < 3){
                sender.sendMessage(ChatColor.RED + "Vous devez fournir au moins un joueur et un montant.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /money <balance|transfer> [joueur] [montant]");
                return true;
            }
            Player toPlayer = null;
            UUID toUUID = null;
            String name = null;
            for(Player pl : Bukkit.getOnlinePlayers()) {
                if(pl.getName().equalsIgnoreCase(args[1])) {
                    toUUID = pl.getUniqueId();
                    toPlayer = pl;
                    name = pl.getName();
                    break;
                }
            }

            if(toUUID == null) {
                for(OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                    if(pl.getName().equalsIgnoreCase(args[1])) {
                        toUUID = pl.getUniqueId();
                        name = pl.getName();
                        break;
                    }
                }
            }

            if(toUUID == null) {
                sender.sendMessage(ChatColor.RED + "Ce joueur n'a pas pu être trouvé.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /money <balance|transfer> [joueur] [montant]");
                return true;
            }

            int amount = 0;
            try {
                amount = Integer.parseInt(args[2]);
            } catch(NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Vous devez fournir un nombre strictement positif.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /money <balance|transfer> [joueur] [montant]");
                return true;
            }

            if(amount <= 0) {
                sender.sendMessage(ChatColor.RED + "Vous devez fournir un nombre strictement positif.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /money <balance|transfer> [joueur] [montant]");
                return true;
            }

            User to = plugin.getUserHandler().getOrCreateUser(toUUID);

            try {
                plugin.getUserHandler().transferMoney(usr, to, amount);
            } catch (HandlersException e) {
                sender.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent.");
                return true;
            }

            player.sendMessage(ChatColor.DARK_AQUA + "[BANQUE] " + ChatColor.AQUA +  "Vous avez envoy\u00E9 "
                    + ChatColor.GREEN + amount + " " + currency + ChatColor.AQUA + " \u00e0 " + ChatColor.GREEN
                    + name + ChatColor.AQUA + ".");

            if(toPlayer != null) {
                toPlayer.sendMessage(ChatColor.DARK_AQUA + "[BANQUE] " + ChatColor.AQUA +  "Vous avez re\u00e7u "
                        + ChatColor.GREEN + amount + " " + currency + ChatColor.AQUA + " de la part de "
                        + ChatColor.GREEN + player.getName() + ChatColor.AQUA + ".");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Vous devez fournir au moins un argument.");
            sender.sendMessage(ChatColor.RED + "Utilisation: /money <balance|transfer> [joueur] [montant]");
            return true;
        }
        return true;
    }

    public static class TabCompleter implements org.bukkit.command.TabCompleter {

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> res = new ArrayList<>();
            if(args.length <= 1) {
                res.add("balance");
                res.add("transfer");
            } else if(args.length == 2 && args[0].equalsIgnoreCase("transfer")) {
                for(Player pl : Bukkit.getOnlinePlayers()) {
                    res.add(pl.getName());
                }
            }
            return res;
        }
    }
}
