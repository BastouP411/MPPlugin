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

package fr.bastoup.mpplugin.dao;

import fr.bastoup.mpplugin.beans.ShopManager;

import java.util.List;
import java.util.UUID;

public interface ShopManagerDAO {
    void create(ShopManager manager);

    void update(ShopManager manager);

    void delete(ShopManager manager);

    void deleteShop(long shop);

    List<ShopManager> getManaged(UUID uuid);

    List<ShopManager> getManaged(String uuid);

    List<ShopManager> getManagers(long shop);

    ShopManager get(long id);
}
