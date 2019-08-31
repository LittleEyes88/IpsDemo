package com.mercku.ipsdemo.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mercku.ipsdemo.activity.SetHouseLayoutScaleActivity
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.model.IpsHouse

/**
 * Created by yanqiong.ran on 2019-08-31.
 */
object CacheUtil {

    fun saveNewHouse(ipsHouse: IpsHouse, context: Context) {
        var acache = AcacheUtil.get(context);
        var cachedData: String? = acache.getAsString(ExtraConstants.EXTRA_HOUSE_LIST)
        var houseList: ArrayList<IpsHouse>? = null
        if (cachedData != null) {
            houseList = Gson().fromJson<ArrayList<IpsHouse>>(cachedData, object : TypeToken<ArrayList<IpsHouse>>() {}.type)
        }
        if (houseList == null || houseList.isEmpty()) {
            houseList = ArrayList<IpsHouse>()
        }
        houseList.add(ipsHouse)
        acache.put(ExtraConstants.EXTRA_HOUSE_LIST, Gson().toJson(houseList))
    }
}