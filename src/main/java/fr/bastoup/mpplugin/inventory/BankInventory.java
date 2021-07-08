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
import fr.bastoup.mpplugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class BankInventory implements InventoryHolder {
    private final Shop shop;
    private final Inventory inv;
    private final MPPlugin plugin;

    public BankInventory(MPPlugin plugin, Shop shop) {
        this.plugin = plugin;
        this.shop = shop;
        this.inv = Bukkit.createInventory(this, 9, shop.getName());

        String currency = plugin.getConfig().getString("currency");

        inv.setItem(2, Util.createItem(
                Material.DIAMOND,
                ChatColor.AQUA + "D\u00e9poser 1 Diamant",
                Collections.singletonList(ChatColor.GREEN + "" + shop.getPrice() + " " + currency)
        ));

        inv.setItem(3, Util.createItem(
                Material.DIAMOND,
                ChatColor.AQUA + "D\u00e9poser 10 Diamants",
                Collections.singletonList(ChatColor.GREEN + "" + 10*shop.getPrice() + " " + currency),
                10
        ));

        inv.setItem(4, Util.createItem(
                Material.DIAMOND,
                ChatColor.AQUA + "D\u00e9poser 32 Diamants",
                Collections.singletonList(ChatColor.GREEN + "" + 32*shop.getPrice() + " " + currency),
                32
        ));

        inv.setItem(5, Util.createItem(
                Material.DIAMOND,
                ChatColor.AQUA + "D\u00e9poser 64 Diamants",
                Collections.singletonList(ChatColor.GREEN + "" + 64*shop.getPrice() + " " + currency),
                64
        ));

        inv.setItem(6, Util.createItem(
                Material.DIAMOND_BLOCK,
                ChatColor.AQUA + "D\u00e9poser tous vos Diamants"
        ));

    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public Shop getShop() {
        return shop;
    }
}
