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
package com.soomla.store.domain.data;

import android.util.Log;
import com.soomla.store.StoreConfig;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * This class represents a pack of the game's virtual currency.
 * For example: If you have a "Coin" as a virtual currency, you might
 * want to sell packs of "Coins". e.g. "10 Coins Set" or "Super Saver Pack".
 * The currency pack usually has a google item related to it. As a developer,
 * you'll define the google item in Google's in-app purchase dashboard.
 */
public class VirtualCurrencyPack extends AbstractVirtualItem {

    /** Constructor
     *
     * @param mName is the name of the virtual currency pack.
     * @param mDescription is the description of the virtual currency pack. This will show up
     *                       in the store in the description section.
     * @param mItemId is the id of the virtual currency pack.
     * @param productId is the product id on Google Market..
     * @param mPrice is the actual $$ cost of the virtual currency pack.
     * @param mCurrencyAmout is the amount of currency in the pack.
     * @param mCurrency is the currency associated with this pack.
     */
    public VirtualCurrencyPack(String mName, String mDescription, String mItemId,
                               String productId, double mPrice, int mCurrencyAmout, VirtualCurrency mCurrency) {
        super(mName, mDescription, mItemId);
        this.mCurrency = mCurrency;
        this.mGoogleItem = new GoogleMarketItem(productId, GoogleMarketItem.Managed.UNMANAGED);
        this.mPrice = mPrice;
        this.mCurrencyAmount = mCurrencyAmout;
    }

    /** Constructor
     *
     * Generates an instance of {@link VirtualCurrencyPack} from a JSONObject.
     * @param jsonObject is a JSONObject representation of the wanted {@link VirtualCurrencyPack}.
     * @throws JSONException
     */
    public VirtualCurrencyPack(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        this.mGoogleItem = new GoogleMarketItem(jsonObject.getString(JSONConsts.CURRENCYPACK_PRODUCT_ID),
                GoogleMarketItem.Managed.UNMANAGED);
        this.mPrice = jsonObject.getDouble(JSONConsts.CURRENCYPACK_PRICE);
        this.mCurrencyAmount = jsonObject.getInt(JSONConsts.CURRENCYPACK_AMOUNT);

        String currencyItemId = jsonObject.getString(JSONConsts.CURRENCYPACK_CURRENCYITEMID);
        try{
            this.mCurrency = StoreInfo.getVirtualCurrencyByItemId(currencyItemId);
        } catch (VirtualItemNotFoundException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "Couldn't find the associated currency.");
            }
        }
    }

    /**
     * Converts the current {@link VirtualCurrencyPack} to a JSONObject.
     * @return a JSONObject representation of the current {@link VirtualCurrencyPack}.
     */
    public JSONObject toJSONObject(){
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONConsts.CURRENCYPACK_PRICE, new Double(mPrice));
            jsonObject.put(JSONConsts.CURRENCYPACK_PRODUCT_ID, mGoogleItem.getProductId());
            jsonObject.put(JSONConsts.CURRENCYPACK_AMOUNT, new Integer(mCurrencyAmount));
            jsonObject.put(JSONConsts.CURRENCYPACK_CURRENCYITEMID, mCurrency.getItemId());

            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }
        } catch (JSONException e) {
            if (StoreConfig.debug){
                Log.d(TAG, "An error occured while generating JSON object.");
            }
        }

        return jsonObject;
    }

    /** Getters **/

    public GoogleMarketItem getmGoogleItem() {
        return mGoogleItem;
    }

    public String getProductId(){
        return mGoogleItem.getProductId();
    }

    public double getPrice() {
        return mPrice;
    }

    public int getCurrencyAmount() {
        return mCurrencyAmount;
    }

    public VirtualCurrency getVirtualCurrency() {
        return mCurrency;
    }

    /** Private members **/

    private static final String TAG = "SOOMLA VirtualCurrencyPack";

    private GoogleMarketItem mGoogleItem;
    private double           mPrice;
    private int              mCurrencyAmount;
    private VirtualCurrency  mCurrency;
}
