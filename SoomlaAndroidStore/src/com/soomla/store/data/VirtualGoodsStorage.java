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

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soomla.store.domain.VirtualGood;

/**
 * This is the storage for the virtual goods.
 */
public class VirtualGoodsStorage {

    /** Constructor
     *
     * @param mPhysicalStorage is the class responsible to persist the data for this storage.
     */
    public VirtualGoodsStorage(IPhysicalStorage mPhysicalStorage) {
        this.mPhysicalStorage = mPhysicalStorage;
        this.mStorage = new HashMap<String, Integer>();
    }

    /** Getters **/

    public int getBalance(VirtualGood vgood){
        storageFromJson(mPhysicalStorage.load());
        if (!mStorage.containsKey(vgood.getItemId())){
            return 0;
        }
		return mStorage.get(vgood.getItemId());
	}

    /** Public functions **/

    /**
    * Adds the given amount of currency to the storage.
    * @param amount is the amount of currency to add.
    */
    public int add(VirtualGood vgood, int amount){
		if (!mStorage.containsKey(vgood.getItemId())){
			mStorage.put(vgood.getItemId(), 0);
		}
		
		mStorage.put(vgood.getItemId(), mStorage.get(vgood.getItemId()) + amount);
        mPhysicalStorage.save(storageToJson());

        return mStorage.get(vgood.getItemId());
	}

    /**
     * Removes the given amount from the given virtual good's balance.
     * @param virtualGood is the virtual good to remove the given amount from.
     * @param amount is the amount to remove.
     */
    public void remove(VirtualGood virtualGood, int amount){
		if (!mStorage.containsKey(virtualGood.getItemId())){
			return;
		}
		
		int balance = mStorage.get(virtualGood.getItemId()) - amount;
		mStorage.put(virtualGood.getItemId(), balance > 0 ? balance : 0);
        mPhysicalStorage.save(storageToJson());
	}

    /** Private functions **/

    private void storageFromJson(String storageJson) {
        ObjectMapper mapper = new ObjectMapper();

        if (storageJson.isEmpty()){
            mStorage = new HashMap<String, Integer>();
        }
        else {
            try {
                mStorage = mapper.readValue(storageJson,
                        new TypeReference<HashMap<String,Integer>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String storageToJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(mStorage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /** Private members **/

    private HashMap<String, Integer> mStorage;
    private IPhysicalStorage mPhysicalStorage;

}
