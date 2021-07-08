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

public class Shop {
    private long id;
    private UUID owner;
    private String name;
    private boolean bank;
    private int price;
    private int x;
    private int y;
    private int z;

    public Shop(long id, UUID owner, String name, boolean bank, int price, int x, int y, int z) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.bank = bank;
        this.price = price;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Shop(long id, String owner, String name, boolean bank, int price, int x, int y, int z) {
        this.id = id;
        this.owner = owner == null ? null : UUID.fromString(owner);
        this.name = name;
        this.bank = bank;
        this.price = price;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Shop(long id, User owner, String name, boolean bank, int price, int x, int y, int z) {
        this.id = id;
        this.owner = owner.getUUID();
        this.name = name;
        this.bank = bank;
        this.price = price;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : UUID.fromString(owner);
    }

    public void setOwner(User owner) {
        this.owner = owner.getUUID();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBank() {
        return bank;
    }

    public void setBank(boolean bank) {
        this.bank = bank;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
