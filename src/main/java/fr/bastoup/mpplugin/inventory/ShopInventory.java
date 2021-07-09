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

package fr.bastoup.mpplugin.inventory;

import fr.bastoup.mpplugin.MPPlugin;
import fr.bastoup.mpplugin.beans.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ShopInventory implements InventoryHolder {
    private final Inventory chest;
    private final Shop shop;
    private final Inventory inv;
    private final MPPlugin plugin;

    public ShopInventory(MPPlugin plugin, Inventory chest, Shop shop) {
        this.plugin = plugin;
        this.chest = chest;
        this.shop = shop;
        this.inv = Bukkit.createInventory(this, chest.getSize(), ChatColor.AQUA + shop.getName());

        ItemStack[] content = chest.getContents();
        for(int i = 0; i < content.length; i++) {
            if(content[i] != null && !content[i].getType().equals(Material.AIR)) {
                ItemStack item = content[i].clone();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(Collections.singletonList(ChatColor.GREEN + Integer.toString(shop.getPrice()) + " "
                        + plugin.getConfig().getString("currency")));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }

    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public Inventory getChest() {
        return chest;
    }

    public Shop getShop() {
        return shop;
    }
}
