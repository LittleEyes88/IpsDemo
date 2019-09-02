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
        var hasExited = false
        if (cachedData != null) {
            houseList = Gson().fromJson<ArrayList<IpsHouse>>(cachedData, object : TypeToken<ArrayList<IpsHouse>>() {}.type)
            var index = 0

            while (index < houseList.size) {
                if (ipsHouse.mId.equals(houseList[index].mId)) {
                    houseList[index] = ipsHouse
                    hasExited = true
                    break;
                }
                index++
            }
        }
        if (houseList == null || houseList.isEmpty()) {
            houseList = ArrayList<IpsHouse>()
        }
        if (!hasExited) {
            houseList.add(ipsHouse)
        }
        acache.put(ExtraConstants.EXTRA_HOUSE_LIST, Gson().toJson(houseList))
    }

    fun saveHouseList(houseList: ArrayList<IpsHouse>, context: Context) {
        var acache = AcacheUtil.get(context);
        acache.put(ExtraConstants.EXTRA_HOUSE_LIST, Gson().toJson(houseList))
    }
}