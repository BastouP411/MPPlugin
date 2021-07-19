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

package fr.bastoup.mpplugin.beans;

import java.util.UUID;

public class User {

    private UUID uuid;
    private long money;
    private Long homeX;
    private Long homeY;
    private Long homeZ;
    private UUID homeWorld;


    public User(UUID uuid, long money) {
        this.uuid = uuid;
        this.money = money;
    }

    public User(String uuid, long money) {
        this.uuid = uuid == null ? null : UUID.fromString(uuid);
        this.money = money;
    }

    public User(String uuid, long money, Long homeX, Long homeY, Long homeZ, String homeWorld) {
        this.uuid = uuid == null ? null : UUID.fromString(uuid);
        this.money = money;
        this.homeX = homeX;
        this.homeY = homeY;
        this.homeZ = homeZ;
        this.homeWorld = homeWorld == null ? null : UUID.fromString(homeWorld);
    }

    public User(UUID uuid, long money, Long homeX, Long homeY, Long homeZ, UUID homeWorld) {
        this.uuid = uuid;
        this.money = money;
        this.homeX = homeX;
        this.homeY = homeY;
        this.homeZ = homeZ;
        this.uuid = homeWorld;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid == null ? null : UUID.fromString(uuid);
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public Long getHomeX() {
        return homeX;
    }

    public void setHomeX(Long homeX) {
        this.homeX = homeX;
    }

    public Long getHomeY() {
        return homeY;
    }

    public void setHomeY(Long homeY) {
        this.homeY = homeY;
    }

    public Long getHomeZ() {
        return homeZ;
    }

    public void setHomeZ(Long homeZ) {
        this.homeZ = homeZ;
    }

    public UUID getHomeWorld() {
        return homeWorld;
    }

    public void setHomeWorld(UUID homeWorld) {
        this.homeWorld = homeWorld;
    }

    public void setHomeWorld(String homeWorld) {
        this.homeWorld = homeWorld == null ? null : UUID.fromString(homeWorld);
    }
}
