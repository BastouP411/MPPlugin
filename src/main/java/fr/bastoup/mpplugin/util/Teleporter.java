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

package fr.bastoup.mpplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Teleporter extends BukkitRunnable {

    private final UUID uuid;
    private final int originalTimer;
    private int timer;
    private final Location dest;
    private final Location origin;

    public Teleporter(Player pl, Location dest, int timer) {
        this.uuid = pl.getUniqueId();
        this.originalTimer = timer;
        this.timer = timer;
        this.origin = pl.getLocation();
        this.dest = dest;
    }

    @Override
    public void run() {
        Player pl = Bukkit.getPlayer(uuid);
        if(pl == null || !pl.isOnline()) {
            cancel();
            return;
        }

        if(timer == originalTimer) {
            pl.sendMessage(ChatColor.DARK_AQUA + "[TELEPORT] " + ChatColor.AQUA
                    + "Vous serez t\u00e9l\u00e9port\u00e9 dans "
                    + ChatColor.GREEN + originalTimer + " secondes" + ChatColor.AQUA + ". Ne bougez pas !");
        }

        double dist = pl.getLocation().distance(origin);
        if(Double.isNaN(dist) || dist > 0.5) {
            pl.sendMessage(ChatColor.DARK_AQUA + "[TELEPORT] " + ChatColor.AQUA + "Vous avez boug\u00e9, t\u00e9l\u00e9portation annul\u00e9e !");
            cancel();
            return;
        }

        if (timer <= 0) {
            pl.sendMessage(ChatColor.DARK_AQUA + "[TELEPORT] " + ChatColor.AQUA + "Woosh !");
            pl.teleport(dest);
            cancel();
            return;
        }
        pl.sendMessage(ChatColor.DARK_AQUA + "[TELEPORT] " + ChatColor.GREEN + timer + "...");
        timer--;
    }

}
