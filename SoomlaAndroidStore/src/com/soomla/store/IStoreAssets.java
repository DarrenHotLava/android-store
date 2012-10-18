/*
 * Copyright (C) 2012 Soomla Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soomla.store;

import com.soomla.store.domain.data.*;

/**
 * This interface represents a single game's metadata.
 * Use this interface to create your assets class that will be transferred to StoreInfo
 * upon initialization.
 */
public interface IStoreAssets {
    /**
     * A representation of your game's virtual currency.
     * @return a representation of your game's virtual currency.
     */
    VirtualCurrency[] getVirtualCurrencies();

    /**
     * An array of all virtual goods served by your store.
     * NOTE: The order of the items in the array will be their order when shown to the user.
     * @return an array of all virtual goods served by your store.
     */
    VirtualGood[] getVirtualGoods();

    /**
     * An array of all virtual currency packs served by your store.
     * NOTE: The order of the items in the array will be their order when shown to the user.
     * @return an array of all virtual currency packs served by your store.
     */
    VirtualCurrencyPack[] getVirtualCurrencyPacks();

    /**
     * An array of all virtual categories served by your store.
     * @return an array of all virtual categories served by your store.
     */
    VirtualCategory[] getVirtualCategories();

    /**
     * You can define managed items that you'd like to use for your needs.
     * UNMANAGED items are usually just currency packs. If you use SOOMLA's storefront, it'll take care of
     * the UNMANAGED for you in the UI.
     * MANAGED items are usually used to let users purchase a "no-ads" token.
     * Make sure you set the type of the items you add here as Managed.MANAGED.
     * @return an array of all managed served in your game.
     */
    GoogleMarketItem[] getGoogleManagedItems();
}
