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

package fr.bastoup.mpplugin.events;

import fr.bastoup.mpplugin.MPPlugin;
import fr.bastoup.mpplugin.beans.Shop;
import fr.bastoup.mpplugin.beans.User;
import fr.bastoup.mpplugin.handlers.HandlersException;
import fr.bastoup.mpplugin.inventory.ShopInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ShopEvents implements Listener {

    private final MPPlugin plugin;

    public ShopEvents(MPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getType().toString().toLowerCase().contains("sign")) {
            Location loc = event.getBlock().getLocation();
            Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            if(shop != null) {
                event.getPlayer().sendMessage(ChatColor.RED + "Ce panneau appartient \u00e0 un shop. Vous ne pouvez pas le d\u00e9truire.");
                event.setCancelled(true);
            }
        }

        if(event.getBlock().getType().equals(Material.CHEST) || event.getBlock().getType().equals(Material.BARREL)) {
            Location loc = event.getBlock().getLocation();
            Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
            if(shop != null && !shop.isBank()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Ce coffre appartient \u00e0 un shop. Vous ne pouvez pas le d\u00e9truire.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc = event.getClickedBlock().getLocation();

            Inventory inv = null;
            if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                Chest chest = (Chest) event.getClickedBlock().getState();
                inv = chest.getBlockInventory();
            } else if (event.getClickedBlock().getType().equals(Material.BARREL)) {
                Barrel barrel = (Barrel) event.getClickedBlock().getState();
                inv = barrel.getInventory();
            } else {
                return;
            }

            Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
            if(shop != null && !shop.isBank() && !event.getPlayer().getUniqueId().equals(shop.getOwner())) {
                ShopInventory gui = new ShopInventory(plugin, inv, shop);
                event.getPlayer().openInventory(gui.getInventory());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if(inv != null && inv.getHolder() instanceof ShopInventory holder) {
            int slot = event.getSlot();
            long shopId = holder.getShop().getId();
            Shop shop = plugin.getShopHandler().getShop(shopId);
            User usr = plugin.getUserHandler().getOrCreateUser(event.getWhoClicked().getUniqueId());

            if(shop == null) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Ce shop n'existe plus.");
                event.getWhoClicked().closeInventory();
                return;
            }

            if(usr.getMoney() < shop.getPrice()) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent.");
                event.getWhoClicked().closeInventory();
                return;
            }

            Block block = new Location(event.getWhoClicked().getWorld(), shop.getX(), shop.getY() - 1, shop.getZ())
                    .getBlock();
            Inventory container = null;
            if (block.getType().equals(Material.CHEST)) {
                Chest chest = (Chest) block.getState();
                container = chest.getBlockInventory();
            } else if (block.getType().equals(Material.BARREL)) {
                Barrel barrel = (Barrel) block.getState();
                container = barrel.getInventory();
            } else {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Le shop a eu un probl\u00e8me.");
                event.getWhoClicked().closeInventory();
                return;
            }

            ItemStack item = container.getItem(slot);
            if(item == null || !item.getType().equals(event.getCurrentItem().getType()) || item.getAmount() != event.getCurrentItem().getAmount()) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Le shop a eu un probl\u00e8me.");
                event.getWhoClicked().closeInventory();
                return;
            }

            try {
                plugin.getUserHandler().transferMoney(usr, plugin.getUserHandler().getOrCreateUser(shop.getOwner()), shop.getPrice());
            } catch (HandlersException e) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent.");
                event.getWhoClicked().closeInventory();
                return;
            }

            String currency = plugin.getConfig().getString("currency");
            container.setItem(slot, new ItemStack(Material.AIR));
            event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), item);
            event.getWhoClicked().closeInventory();

            event.getWhoClicked().sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA + "Vous avez d\u00e9pens\u00e9 "
                    + ChatColor.GREEN + shop.getPrice() + " " + currency + ChatColor.AQUA + " dans le shop "
                    + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");

            Player owner = Bukkit.getPlayer(shop.getOwner());
            if(owner != null) {
                owner.sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA + "Vous avez re\u00e7u "
                        + ChatColor.GREEN + shop.getPrice() + " " + currency + ChatColor.AQUA + " de votre shop "
                        + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");
            }

        }
    }


}
