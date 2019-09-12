package com.mercku.ipsdemo.view


import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.util.BitmapUtil
import com.mercku.ipsdemo.util.MathUtil
import com.mercku.ipsdemo.util.MathUtil.distance
import java.io.File
import kotlin.math.sqrt

/**
 * Created by yanqiong.ran on 2019-07-19.
 */
class MyLocatorView : BaseEditView {
    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLUE)
        drawHouseDetail(canvas)
    }

    private fun drawHouseDetail(canvas: Canvas) {

        drawHouseBitmap(canvas)
        drawAllLocator(canvas)
        drawLineBetweenAnyTwoPoints(canvas)
    }

    private fun drawLineBetweenAnyTwoPoints(canvas: Canvas) {
        var index = 0
        var startX = getImgLeftAfterTransOrScale()
        var startY = getImgTopAfterTransOrScale()
        while (index < mHouseDetail!!.mData!!.size) {
            val locator = mHouseDetail!!.mData!![index]
            val unit = mHouseDetail!!.mBitmapActualWidth / (mHouseBitmap!!.width * mTotalScaled)
            val curX = startX + mHouseBitmap!!.width * locator.mLocationActual.x * mTotalScaled
            val curDisX = curX * unit
            val curY = startY + mHouseBitmap!!.height * locator.mLocationActual.y * mTotalScaled
            val curDisY = curY * unit

            if (locator.mIsSelected || locator.mIsAdded) {
                var nextIndex = index + 1
                while (nextIndex < mHouseDetail!!.mData!!.size) {
                    val nextLocator = mHouseDetail!!.mData!![nextIndex]
                    if (nextLocator.mIsSelected || nextLocator.mIsAdded) {
                        val nextX = startX + mHouseBitmap!!.width * nextLocator.mLocationActual.x * mTotalScaled
                        val nextDisX = nextX * unit
                        val nextY = startY + mHouseBitmap!!.height * nextLocator.mLocationActual.y * mTotalScaled
                        val nextDisY = nextY * unit

                        var actualDis = MathUtil.distance(curDisX, curDisY, nextDisX, nextDisY)
                        var pixDis = MathUtil.distance(curX, curY, nextX, nextY)

                        canvas.drawLine(curX, curY, nextX, nextY, mLinePaint)
                        val path = Path()
                        path.moveTo(curX, curY)
                        path.lineTo(nextX, nextY)
                        val disStr: String = String.format("%.1f", actualDis)
                        canvas.drawTextOnPath(disStr + "m", path, pixDis / 2, 0f, mTextPaint)
                    }
                    nextIndex++
                }
            }
            index++

        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHouseBitmap?.recycle()
        mDotBitmap?.recycle()
    }

}
