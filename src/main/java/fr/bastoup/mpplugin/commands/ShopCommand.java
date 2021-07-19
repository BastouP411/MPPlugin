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
import fr.bastoup.mpplugin.beans.Shop;
import fr.bastoup.mpplugin.beans.ShopManager;
import fr.bastoup.mpplugin.beans.User;
import fr.bastoup.mpplugin.handlers.HandlersException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopCommand implements CommandExecutor {

    private final MPPlugin plugin;

    public ShopCommand(MPPlugin plugin) {
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
            sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
            return true;
        }

        String currency = plugin.getConfig().getString("currency");
        Block block = player.getTargetBlockExact(5);
        if(block == null) {
            sender.sendMessage(ChatColor.RED + "Vous devez regarder un panneau.");
            sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
            return true;
        }
        Location loc = block.getLocation();
        User usr = plugin.getUserHandler().getOrCreateUser(player.getUniqueId());
        if(args[0].equalsIgnoreCase("remove")) {
            try {
                Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                if(shop == null) {
                    sender.sendMessage(ChatColor.RED + "Un shop n'existe pas \u00E0 cette position.");
                    sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                    return true;
                }

                if(shop.getOwner() == null || !shop.getOwner().equals(player.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "Vous ne poss\u00E9dez pas ce shop.");
                    sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                    return true;
                }
                plugin.getShopHandler().removeShop(player.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                player.sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA +  "Vous avez supprim\u00E9 le shop "
                        + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");
            } catch (HandlersException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }
        } else if(args[0].equalsIgnoreCase("create")) {
            if(args.length < 3){
                sender.sendMessage(ChatColor.RED + "Vous devez fournir au moins un nom et un prix.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            if(args[1].length() == 0 || args[1].length() > 15) {
                sender.sendMessage(ChatColor.RED + "Le nom du shop doit faire moins de 15 caract\u00E8res.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            int price = 0;
            try {
                price = Integer.parseInt(args[2]);
            } catch(NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Vous devez fournir un nombre strictement positif.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            if(price <= 0) {
                sender.sendMessage(ChatColor.RED + "Vous devez fournir un nombre strictement positif.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            try {
                plugin.getShopHandler().createShop(player.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), player.getUniqueId(), args[1], price);
            } catch (HandlersException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            player.sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA +  "Vous avez cr\u00E9\u00E9 le shop "
                    + ChatColor.GREEN + args[1] + ChatColor.AQUA + " au prix de " + ChatColor.GREEN
                    + price + " " + currency + ChatColor.AQUA + ".");

        } else if(args[0].equalsIgnoreCase("manager")) {
            if(args.length < 2){
                sender.sendMessage(ChatColor.RED + "Vous devez fournir au moins un nom de joueur.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            if(shop == null) {
                sender.sendMessage(ChatColor.RED + "Un shop n'existe pas \u00E0 cette position.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            if(shop.getOwner() == null || !shop.getOwner().equals(player.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "Vous ne poss\u00E9dez pas ce shop.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            UUID uuid = null;
            String name = null;
            for(Player pl : Bukkit.getOnlinePlayers()) {
                if(pl.getName().equalsIgnoreCase(args[1])) {
                    uuid = pl.getUniqueId();
                    name = pl.getName();
                    break;
                }
            }

            if(uuid == null) {
                for(OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                    if(pl.getName().equalsIgnoreCase(args[1])) {
                        uuid = pl.getUniqueId();
                        name = pl.getName();
                        break;
                    }
                }
            }

            if(uuid == null) {
                sender.sendMessage(ChatColor.RED + "Ce joueur n'a pas pu être trouvé.");
                sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
                return true;
            }

            List<ShopManager> managers = plugin.getShopHandler().getManagers(shop);
            for(ShopManager manager : managers) {
                if(manager.getUser().equals(uuid)) {
                    plugin.getDAOFactory().getShopManagerDAO().delete(manager);
                    player.sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.GREEN + name + ChatColor.AQUA
                            + " n'est plus manager du shop " + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");
                    return true;
                }
            }

            plugin.getDAOFactory().getShopManagerDAO().create(new ShopManager(0, shop.getId(), uuid));
            player.sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.GREEN + name + ChatColor.AQUA
                    + " est devenu manager du shop " + shop.getName() + ChatColor.AQUA + ".");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Vous devez fournir au moins un argument.");
            sender.sendMessage(ChatColor.RED + "Utilisation: /shop <create|remove|manager> [nom] [prix]");
            return true;
        }
        return true;
    }

    public static class TabCompleter implements org.bukkit.command.TabCompleter {

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> res = new ArrayList<>();
            if(args.length <= 1) {
                res.add("create");
                res.add("remove");
                res.add("manager");
            } else if(args.length == 2 && args[0].equalsIgnoreCase("manager")) {
                for(Player pl : Bukkit.getOnlinePlayers()) {
                    res.add(pl.getName());
                }
            }
            return res;
        }
    }
}
