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

package com.soomla.store.data;

import android.database.Cursor;
import android.util.Log;
import com.soomla.billing.util.AESObfuscator;
import com.soomla.store.BusProvider;
import com.soomla.store.StoreConfig;
import com.soomla.store.domain.data.VirtualCurrency;
import com.soomla.store.events.CurrencyBalanceChangedEvent;

/**
 * This class provide basic storage operations on VirtualCurrencies.
 */
public class VirtualCurrencyStorage {

    /** Constructor
     *
     */
    public VirtualCurrencyStorage() {
    }

    /** Public functions **/

    /**
     * Fetch the balance of the given virtual currency.
     * @param virtualCurrency is the required virtual currency.
     * @return the balance of the required virtual currency.
     */
    public int getBalance(VirtualCurrency virtualCurrency){
        if (StoreConfig.debug){
            Log.d(TAG, "trying to fetch balance for virtual currency");
        }

        String itemId = virtualCurrency.getItemId();
        String key = KeyValDatabase.keyCurrencyBalance(itemId);
        key = StorageManager.getAESObfuscator().obfuscateString(key);

        String balanceStr = StorageManager.getDatabase().getKeyVal(key);
        int balance = 0;
        if (balanceStr != null) {
            try {
                balance = StorageManager.getAESObfuscator().unobfuscateToInt(balanceStr);
            } catch (AESObfuscator.ValidationException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        if (StoreConfig.debug){
            Log.d(TAG, "the currency balance is " + balance);
        }
        return balance;
    }


    public int setBalance(VirtualCurrency virtualCurrency, int balance) {
        if (StoreConfig.debug){
            Log.d(TAG, "setting balance " + balance + " to " + virtualCurrency.getName() + ".");
        }

        int oldBalance = getBalance(virtualCurrency);
        if (oldBalance == balance) {
            return balance;
        }

        String itemId = virtualCurrency.getItemId();

        String balanceStr = "" + balance;
        String key = KeyValDatabase.keyCurrencyBalance(itemId);
        balanceStr = StorageManager.getAESObfuscator().obfuscateString(balanceStr);
        key        = StorageManager.getAESObfuscator().obfuscateString(key);
        StorageManager.getDatabase().setKeyVal(key, balanceStr);

        BusProvider.getInstance().post(new CurrencyBalanceChangedEvent(virtualCurrency, balance, 0));

        return balance;
    }

    /**
     * Adds the given amount of currency to the storage.
     * @param virtualCurrency is the required virtual currency.
     * @param amount is the amount of currency to add.
     * @return the new balance after adding amount.
     */
    public int add(VirtualCurrency virtualCurrency, int amount){
        if (StoreConfig.debug){
            Log.d(TAG, "adding " + amount + " currencies.");
        }

        int balance = getBalance(virtualCurrency);
        String balanceStr = "" + (balance + amount);
        String itemId = virtualCurrency.getItemId();
        String key = KeyValDatabase.keyCurrencyBalance(itemId);
        balanceStr = StorageManager.getAESObfuscator().obfuscateString(balanceStr);
        key      = StorageManager.getAESObfuscator().obfuscateString(key);
        StorageManager.getDatabase().setKeyVal(key, balanceStr);

        BusProvider.getInstance().post(new CurrencyBalanceChangedEvent(virtualCurrency, balance+amount, amount));

        return balance + amount;
    }

    /**
     * Removes the given amount of currency from the storage.
     * @param virtualCurrency is the required virtual currency.
     * @param amount is the amount of currency to remove.
     */
    public int remove(VirtualCurrency virtualCurrency, int amount){
        if (StoreConfig.debug){
            Log.d(TAG, "removing " + amount + " currencies.");
        }

        String itemId = virtualCurrency.getItemId();
        int balance = getBalance(virtualCurrency) - amount;
        balance = balance > 0 ? balance : 0;
        String balanceStr = "" + balance;
        String key = KeyValDatabase.keyCurrencyBalance(itemId);
        balanceStr = StorageManager.getAESObfuscator().obfuscateString(balanceStr);
        key      = StorageManager.getAESObfuscator().obfuscateString(key);
        StorageManager.getDatabase().setKeyVal(key, balanceStr);

        BusProvider.getInstance().post(new CurrencyBalanceChangedEvent(virtualCurrency, balance, -1*amount));

        return balance;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualCurrencyStorage";

}
