package com.mercku.ipsdemo


import android.graphics.Matrix
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View

/**
 * Created by yanqiong.ran on 2019-08-28.
 */


class DotMoveListener(private val mTargetView: View) : View.OnTouchListener {

    /**
     * 记录是拖拉照片模式还是放大缩小照片模式
     */
    private var mMode = 0// 初始状态

    /**
     * 用于记录开始时候的坐标位置
     */
    private val mStartPoint = PointF()

    /**
     * 手指点击屏幕的触摸事件
     */
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255  */
        when (event.action and MotionEvent.ACTION_MASK) {
            // 手指压下屏幕
            MotionEvent.ACTION_DOWN -> {
                mMode = MODE_DRAG
                // 记录ImageView当前的移动位置
                mStartPoint.set(event.x, event.y)
            }
            // 手指在屏幕上移动，改事件会被不断触发
            MotionEvent.ACTION_MOVE ->
                // 拖拉图片
                if (mMode == MODE_DRAG) {
                    val dx = event.x - mStartPoint.x // 得到x轴的移动距离
                    val dy = event.y - mStartPoint.y // 得到x轴的移动距离
                    // 在没有移动之前的位置上进行移动
                    mTargetView.translationX += dx
                    mTargetView.translationY += dy
                    mTargetView.isSelected = true
                }
            // 手指离开屏幕
            MotionEvent.ACTION_UP,
                // 当触点离开屏幕，但是屏幕上还有触点(手指)
            MotionEvent.ACTION_POINTER_UP,
                // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            MotionEvent.ACTION_POINTER_DOWN -> {
                mMode = 0
                mTargetView.isSelected = false
            }
        }
        return true
    }


    companion object {
        /**
         * 拖拉照片模式
         */
        private val MODE_DRAG = 1

    }

}