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
import fr.bastoup.mpplugin.inventory.BankInventory;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BankEvents implements Listener {

    private final MPPlugin plugin;

    public BankEvents(MPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc = event.getClickedBlock().getLocation();
            if (!event.getClickedBlock().getType().toString().toLowerCase().contains("sign")) {
                return;
            }

            Shop shop = plugin.getShopHandler().getShop(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            if(shop != null && shop.isBank()) {
                BankInventory gui = new BankInventory(plugin, shop);
                event.getPlayer().openInventory(gui.getInventory());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if(inv != null && inv.getHolder() instanceof BankInventory holder) {
            event.setCancelled(true);
            int slot = event.getSlot();
            int number = 0;
            boolean all = false;
            switch(slot) {
                case 2:
                    number = 1;
                    break;
                case 3:
                    number = 10;
                    break;
                case 4:
                    number = 32;
                    break;
                case 5:
                    number = 64;
                    break;
                case 6:
                    all = true;
                    break;
                default:
                    return;
            }

            int foundDiamonds = 0;
            PlayerInventory plInv = event.getWhoClicked().getInventory();
            ItemStack[] storage = plInv.getStorageContents();
            for(int i = 0; i < storage.length; i++) {
                if(storage[i] != null && storage[i].getType().equals(Material.DIAMOND)) {
                    if(storage[i].getAmount() + foundDiamonds >= number && !all) {
                        int target = number - foundDiamonds;
                        int rest = storage[i].getAmount() - target;
                        foundDiamonds = number;

                        storage[i].setAmount(rest);
                        break;
                    } else {
                        foundDiamonds += storage[i].getAmount();
                        storage[i] = null;
                    }
                }
            }
            plInv.setStorageContents(storage);

            User usr = plugin.getUserHandler().getOrCreateUser(event.getWhoClicked().getUniqueId());
            try {
                plugin.getUserHandler().addMoney(usr, holder.getShop().getPrice() * foundDiamonds);
            } catch (HandlersException e) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Une erreur est survenue.");
            }

            String currency = plugin.getConfig().getString("currency");
            event.getWhoClicked().sendMessage(ChatColor.DARK_AQUA + "[BANQUE] " + ChatColor.AQUA
                    + "Vous avez d\u00e9pos\u00e9 " + ChatColor.GREEN + "" + foundDiamonds + " diamants "
                    + ChatColor.AQUA + "et re\u00e7u " + ChatColor.GREEN + ""
                    + (holder.getShop().getPrice() * foundDiamonds) + " " + currency + ChatColor.AQUA + ".");
        }
    }


}
