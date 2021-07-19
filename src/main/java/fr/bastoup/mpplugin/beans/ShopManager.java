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

public class ShopManager {
    private long id;
    private long shop;
    private UUID user;

    public ShopManager(long id, long shop, UUID user) {
        this.id = id;
        this.shop = shop;
        this.user = user;
    }

    public ShopManager(long id, long shop, String user) {
        this.id = id;
        this.shop = shop;
        this.user = UUID.fromString(user);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getShop() {
        return shop;
    }

    public void setShop(long shop) {
        this.shop = shop;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public void setUser(String user) {
        this.user = user == null ? null : UUID.fromString(user);
    }
}
