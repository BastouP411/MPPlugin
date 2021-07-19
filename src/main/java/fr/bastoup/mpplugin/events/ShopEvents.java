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
import fr.bastoup.mpplugin.beans.ShopManager;
import fr.bastoup.mpplugin.beans.User;
import fr.bastoup.mpplugin.handlers.HandlersException;
import fr.bastoup.mpplugin.handlers.ShopHandler;
import fr.bastoup.mpplugin.inventory.ShopInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class ShopEvents implements Listener {
    
    private static final BlockFace[] HORIZ_FACES = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

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
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType().equals(Material.HOPPER)) {
            if(event.getBlockAgainst().getType().equals(Material.CHEST) || event.getBlockAgainst().getType().equals(Material.BARREL)) {
                Location loc = event.getBlockAgainst().getLocation();
                Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
                if(shop != null && !shop.getOwner().equals(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas placer d'entonnoir sur un shop qui ne vous appartiens pas.");
                    event.setCancelled(true);
                    return;
                }
            }

            Location blockLoc = event.getBlock().getLocation();

            Block blockTop = new Location(blockLoc.getWorld(), blockLoc.getBlockX(), blockLoc.getBlockY() + 1, blockLoc.getBlockZ()).getBlock();
            if(blockTop.getType().equals(Material.CHEST) || blockTop.getType().equals(Material.BARREL)) {
                Location loc = blockTop.getLocation();
                Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
                if(shop != null && !shop.getOwner().equals(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas placer d'entonnoir sur un shop qui ne vous appartiens pas.");
                    event.setCancelled(true);
                    return;
                }
            }

            Block blockBottom = new Location(blockLoc.getWorld(), blockLoc.getBlockX(), blockLoc.getBlockY() - 1, blockLoc.getBlockZ()).getBlock();
            if(blockBottom.getType().equals(Material.CHEST) || blockBottom.getType().equals(Material.BARREL)) {
                Location loc = blockBottom.getLocation();
                Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
                if(shop != null && !shop.getOwner().equals(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas placer d'entonnoir sur un shop qui ne vous appartiens pas.");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if(event.getBlock().getType().equals(Material.CHEST)) {
            for(BlockFace face : HORIZ_FACES) {
                Block block = event.getBlock().getRelative(face);
                if(block.getType() == Material.CHEST) {
                    Location loc = block.getLocation();
                    Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
                    if(shop != null && !shop.getOwner().equals(event.getPlayer().getUniqueId())) {
                        event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas cr\u00e9er de double coffre avec un shop qui ne vous appartient pas.");
                        event.setCancelled(true);
                    }
                }
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

            if(shop == null)
                return;

            boolean openShop = true;
            boolean owns = false;

            if(shop.isBank()) {
                openShop = false;
            } else if(event.getPlayer().getUniqueId().equals(shop.getOwner())){
                openShop = false;
                owns = true;
            } else {
                List<ShopManager> managers = plugin.getShopHandler().getManagers(shop);
                for(ShopManager m : managers) {
                    if(event.getPlayer().getUniqueId().equals(m.getUser())) {
                        openShop = false;
                        owns = true;
                        break;
                    }
                }
            }

            if(openShop) {
                ShopInventory gui = new ShopInventory(plugin, inv, shop);
                event.getPlayer().openInventory(gui.getInventory());
                event.setCancelled(true);
            } else if(owns) {
                long stock = shop.getStock();
                if(stock > 0) {
                    User usr = plugin.getUserHandler().getOrCreateUser(event.getPlayer().getUniqueId());
                    String currency = plugin.getConfig().getString("currency");
                    try {
                        plugin.getUserHandler().addMoney(usr, stock);
                    } catch (HandlersException e) {
                        event.getPlayer().sendMessage(ChatColor.RED + "Le shop a eu un probl\u00e8me.");
                        e.printStackTrace();
                    }
                    plugin.getShopHandler().resetShopMoney(shop);
                    event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA + "Vous avez r\u00e9cup\u00e9r\u00e9 "
                            + ChatColor.GREEN + stock + " " + currency + ChatColor.AQUA + " dans votre shop "
                            + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");
                }
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
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                return;
            }

            if(usr.getMoney() < shop.getPrice()) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent.");
                event.setCancelled(true);
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
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                return;
            }

            ItemStack item = container.getItem(slot);
            if(item == null || !item.getType().equals(event.getCurrentItem().getType()) || item.getAmount() != event.getCurrentItem().getAmount()) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Le shop a eu un probl\u00e8me.");
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                return;
            }

            try {
                plugin.getUserHandler().removeMoney(usr, shop.getPrice());
                plugin.getShopHandler().addShopMoney(shop, shop.getPrice());
            } catch (HandlersException e) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent.");
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                return;
            }

            event.setCancelled(true);
            String currency = plugin.getConfig().getString("currency");
            container.setItem(slot, new ItemStack(Material.AIR));
            event.getClickedInventory().setItem(slot, new ItemStack(Material.AIR));
            event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), item);

            event.getWhoClicked().sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA + "Vous avez d\u00e9pens\u00e9 "
                    + ChatColor.GREEN + shop.getPrice() + " " + currency + ChatColor.AQUA + " dans le shop "
                    + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");


            Player owner = Bukkit.getPlayer(shop.getOwner());
            if(owner != null) {
                owner.sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA + "Vous avez re\u00e7u "
                        + ChatColor.GREEN + shop.getPrice() + " " + currency + ChatColor.AQUA + " dans votre shop "
                        + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");
            }

            List<ShopManager> managers = plugin.getShopHandler().getManagers(shop);

            for (ShopManager manager: managers) {
                Player man = Bukkit.getPlayer(manager.getUser());
                if(man != null) {
                    man.sendMessage(ChatColor.DARK_AQUA + "[SHOP] " + ChatColor.AQUA + "Vous avez re\u00e7u "
                            + ChatColor.GREEN + shop.getPrice() + " " + currency + ChatColor.AQUA + " dans votre shop "
                            + ChatColor.GREEN + shop.getName() + ChatColor.AQUA + ".");
                }
            }

        }
    }


}
