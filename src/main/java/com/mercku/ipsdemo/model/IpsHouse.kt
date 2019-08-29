package com.mercku.ipsdemo.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by yanqiong.ran on 2019-08-29.
 */
data class IpsHouse(var mData: ArrayList<IpsLocator>?, var mName: String?, val mId: String, var mImageFilePath: String) : Parcelable {
    var mBitmapActualWidth: Float = 0f

    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(IpsLocator.CREATOR),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
        mBitmapActualWidth = parcel.readFloat()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(mData)
        parcel.writeString(mName)
        parcel.writeString(mId)
        parcel.writeString(mImageFilePath)
        parcel.writeFloat(mBitmapActualWidth)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IpsHouse> {
        override fun createFromParcel(parcel: Parcel): IpsHouse {
            return IpsHouse(parcel)
        }

        override fun newArray(size: Int): Array<IpsHouse?> {
            return arrayOfNulls(size)
        }
    }


}