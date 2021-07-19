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
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Collection;

public class GeneralEvents implements Listener {

    private final MPPlugin plugin;

    private int sleepers = 0;

    public GeneralEvents(MPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getScoreboardHandler().updateScoreboard(event.getPlayer());
        updateBed();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        updateBed();
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        updateBed();
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if(!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK))
            return;

        sleepers++;
        updateBed();

    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        sleepers--;
        if(sleepers < 0) {
            sleepers = 0;
        } else {
            updateBed();
        }
    }

    private void updateBed() {
        double limit = plugin.getConfig().getDouble("bedRate");
        String level = (String) plugin.getServerProperties().get("level-name");
        World world = Bukkit.getWorld(level);
        Collection<? extends Player> players = world.getPlayers();
        int count = players.size();
        double rate = ((double) sleepers)/((double) count);
        long percent = Math.round(rate * 100);
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "[SLEEP] " + ChatColor.GREEN + sleepers + "/" + count + " ("
                + percent + "%) " + ChatColor.AQUA + "joueurs sont en train de dormir.");
        if(rate >= limit) {
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "[SLEEP] " + ChatColor.AQUA + "C'est le matin ! On se r\u00e9veille !");
            sleepers = 0;
            for(Player pl : players) {
                if(pl.isSleeping())
                    pl.wakeup(true);
            }

            world.setTime(0);
        }
    }

}
