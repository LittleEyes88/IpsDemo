package com.mercku.ipsdemo.model

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

data class IpsLocator(var mName: String?, var mType: String, var mId: String) : Parcelable {
    var mLocation: PointF = PointF(-1f, -1f)
    var mIsSelected: Boolean = false
    var mIsAdded: Boolean = false

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
        mLocation = parcel.readParcelable(PointF::class.java.classLoader)
        mIsSelected = parcel.readByte() != 0.toByte()
        mIsAdded = parcel.readByte() != 0.toByte()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mName)
        parcel.writeString(mType)
        parcel.writeString(mId)
        parcel.writeParcelable(mLocation, flags)
        parcel.writeByte(if (mIsSelected) 1 else 0)
        parcel.writeByte(if (mIsAdded) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IpsLocator> {
        override fun createFromParcel(parcel: Parcel): IpsLocator {
            return IpsLocator(parcel)
        }

        override fun newArray(size: Int): Array<IpsLocator?> {
            return arrayOfNulls(size)
        }
    }
}
