package com.mercku.ipsdemo.listener


import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Created by yanqiong.ran on 2019-08-28.
 */


class OnViewTouchListener(private val mTargetView: View) : View.OnTouchListener {

    /**
     * 记录是拖拉照片模式还是放大缩小照片模式
     */
    private var mMode = 0// 初始状态

    /**
     * 用于记录开始时候的坐标位置
     */
    private val mStartPoint = PointF()
    /**
     * 用于记录拖拉图片移动的坐标位置
     */
    private val mMatrix = Matrix()
    /**
     * 用于记录图片要进行拖拉时候的坐标位置
     */
    private val mCurrentMatrix = Matrix()

    /**
     * 两个手指的开始距离
     */
    private var mStartDis: Float = 0.toFloat()
    /**
     * 两个手指的中间点
     */
    private var mMidPoint: PointF? = null

    private var mTotalScaled = 1f// 初始scale倍数
    private var mTotalDx = 0f
    private var mTotalDy = 0f
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
                //mCurrentMatrix.set(mImageView.imageMatrix)
                mStartPoint.set(event.x, event.y)
            }
            // 手指在屏幕上移动，改事件会被不断触发
            MotionEvent.ACTION_MOVE ->
                // 拖拉图片
                if (mMode == MODE_DRAG) {
                    val dx = event.x - mStartPoint.x // 得到x轴的移动距离
                    val dy = event.y - mStartPoint.y // 得到x轴的移动距离
                    // 在没有移动之前的位置上进行移动
                    mMatrix.set(mCurrentMatrix)
                    mTotalDx += dx
                    mTotalDy += dy
                    // mMatrix.postTranslate(dx, dy)
                    mTargetView.translationX += dx
                    mTargetView.translationY += dy
                    mTargetView.invalidate()

                } else if (mMode == MODE_ZOOM) {
                    val endDis = distance(event)// 结束距离
                    if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        val scale = endDis / mStartDis// 得到缩放倍数
                        mMatrix.set(mCurrentMatrix)
                        mTotalScaled *= scale
                        //mMatrix.postScale(scale, scale, mMidPoint!!.x, mMidPoint!!.y)

                        Log.d("ryq", " mTargetView.scaleX=" + mTargetView.scaleX +
                                " mTargetView.translationX=" + mTargetView.translationX + " mTargetView.x=" + mTargetView.x)
                        Log.d("ryq", "mTargetView.pivotX=" + mTargetView.pivotX + " mTargetView.pivotY=" + mTargetView.pivotY)
                        Log.d("ryq", " mTargetView.width=" + mTargetView.width)
                        if (mTargetView.scaleX * scale > 0.5) {
                            mTargetView.scaleX *= scale
                            mTargetView.scaleY *= scale
                        }
                        mTargetView.invalidate()
                    }
                }// 放大缩小图片
            // 手指离开屏幕
            MotionEvent.ACTION_UP,
                // 当触点离开屏幕，但是屏幕上还有触点(手指)
            MotionEvent.ACTION_POINTER_UP -> mMode = 0
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            MotionEvent.ACTION_POINTER_DOWN -> {
                mMode = MODE_ZOOM
                /** 计算两个手指间的距离  */
                mStartDis = distance(event)
                /** 计算两个手指间的中间点  */
                if (mStartDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                    mMidPoint = mid(event)
                    //记录当前ImageView的缩放倍数
                    mCurrentMatrix.set(mTargetView.matrix)
                }
            }
        }
//        mImageView.imageMatrix = mMatrix
        return true
    }

    /**
     * 计算两个手指间的距离
     */
    private fun distance(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        /** 使用勾股定理返回两点之间的距离  */
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

    }

    /**
     * 计算两个手指间的中间点
     */
    private fun mid(event: MotionEvent): PointF {
        val midX = (event.getX(1) + event.getX(0)) / 2
        val midY = (event.getY(1) + event.getY(0)) / 2
        return PointF(midX, midY)
    }

    public fun getTotalScaled(): Float {
        return mTotalScaled
    }

    public fun getTotalDx(): Float {
        return mTotalDx
    }

    public fun getTotalDy(): Float {
        return mTotalDy
    }

    companion object {
        /**
         * 拖拉照片模式
         */
        private val MODE_DRAG = 1
        /**
         * 放大缩小照片模式
         */
        private val MODE_ZOOM = 2
    }

}