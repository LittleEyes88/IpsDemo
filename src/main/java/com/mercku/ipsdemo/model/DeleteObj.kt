package com.mercku.ipsdemo.model


/**
 * Created by Administrator on 2018/1/26.
 */

open class DeleteObj {

    // 该数据表示在删除时间限制的时候是否被选择
    @JsonOptional
    protected var mIsSelected: Boolean? = null

    var isSelected: Boolean
        get() = if (mIsSelected == null) false else mIsSelected!!
        set(isSelected) {
            mIsSelected = isSelected
        }
}
