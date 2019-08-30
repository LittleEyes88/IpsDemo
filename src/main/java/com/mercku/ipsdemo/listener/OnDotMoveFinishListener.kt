package com.mercku.ipsdemo.listener

import android.view.View

interface OnDotMoveFinishListener {
    fun onFinish(x: Float, y: Float, id: String, targetView: View)
}