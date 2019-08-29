package com.mercku.ipsdemo.model

import android.graphics.PointF

data class IpsLocator(var mName: String?, var mType: String) {
    var mLocation: PointF = PointF()
    var mIsSelected: Boolean = false
    var mIsAdded: Boolean = false
}
