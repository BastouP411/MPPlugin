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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.UUID;

public class ShopHandler {

    private final MPPlugin plugin;

    public ShopHandler(MPPlugin plugin) {
        this.plugin = plugin;
    }

    public Shop getShop(int x, int y, int z) {
        return plugin.getDAOFactory().getShopDAO().get(x, y, z);
    }

    public Shop getShop(long id) {
        return plugin.getDAOFactory().getShopDAO().get(id);
    }

    public Shop getShop(UUID owner, String name) {
        return plugin.getDAOFactory().getShopDAO().getUserShop(owner, name);
    }

    public Shop removeShop(World world, int x, int y, int z) throws HandlersException {
        Shop shop = getShop(x, y, z);
        if(shop == null) {
            throw new HandlersException("Un shop n'existe pas \u00E0 cette position.");
        }

        plugin.getDAOFactory().getShopDAO().delete(shop);

        Block block = world.getBlockAt(x, y, z);
        if(block.getType().toString().toLowerCase().contains("sign")) {
            Sign sign = (Sign)block.getState();
            sign.setLine(0, "");
            sign.setLine(1, "");
            sign.setLine(2, "");
            sign.setLine(3, "");
            sign.update();
        }
        return shop;
    }

    public Shop createShop(World world, int x, int y, int z, UUID owner, String name, int price) throws HandlersException {
        User usr = plugin.getUserHandler().getOrCreateUser(owner);
        if(getShop(x, y, z) != null) {
            throw new HandlersException("Ce panneau est d\u00E9j\u00E0 associ\u00E9 \u00E0 un shop.");
        }
        Block blockTop = world.getBlockAt(x, y, z);
        Block blockBottom = world.getBlockAt(x, y -1, z);

        if(!(blockTop.getType().toString().toLowerCase().contains("sign") && (blockBottom.getType().equals(Material.CHEST) || blockBottom.getType().equals(Material.BARREL)))) {
            throw new HandlersException("Shop invalide.");
        }

        if(name.length() == 0 || name.length() > 13) {
            throw new HandlersException("Le nom du shop doit faire moins de 13 caract\u00E8res.");
        }

        if(getShop(owner, name) != null) {
            throw new HandlersException("Un shop avec ce nom existe d\u00E9j\u00E0.");
        }

        String currency = plugin.getConfig().getString("currency");
        Sign sign = (Sign)blockTop.getState();
        sign.setLine(0, ChatColor.AQUA + "[" + name + "]");
        sign.setLine(1, "");
        sign.setLine(2, ChatColor.GREEN + Integer.toString(price) + " " + currency);
        sign.setLine(3, "");
        sign.setGlowingText(false);
        sign.update();
        Shop shop = new Shop(0, owner, name, false, price, x, y, z);
        plugin.getDAOFactory().getShopDAO().create(shop);
        return shop;
    }

    public Shop createBank(World world, int x, int y, int z, String name, int price) throws HandlersException {

        if(getShop(x, y, z) != null) {
            throw new HandlersException("Ce panneau est d\u00E9j\u00E0 associ\u00E9 \u00E0 un shop.");
        }
        Block blockTop = world.getBlockAt(x, y, z);

        if(!blockTop.getType().toString().toLowerCase().contains("sign")) {
            throw new HandlersException("Banque invalide.");
        }

        if(name.length() == 0 || name.length() > 15) {
            throw new HandlersException("Le nom d'une banque doit faire moins de 15 caract\u00E8res.");
        }

        if(getShop(null, name) != null) {
            throw new HandlersException("Une banque avec ce nom existe d\u00E9j\u00E0.");
        }

        String currency = plugin.getConfig().getString("currency");
        Sign sign = (Sign)blockTop.getState();
        sign.setLine(0, ChatColor.DARK_PURPLE + "[BANQUE]");
        sign.setLine(1, "");
        sign.setLine(2, ChatColor.LIGHT_PURPLE + Integer.toString(price) + " " + currency);
        sign.setLine(3, "");
        sign.setGlowingText(false);
        sign.update();
        Shop shop = new Shop(0, (String)null, name, true, price, x, y, z);
        plugin.getDAOFactory().getShopDAO().create(shop);
        return shop;
    }
}
