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
        Log.d("ryq", "drawHouseDetail  mHouseDetail!!.mBitmapActualWidth=" + mHouseDetail!!.mBitmapActualWidth)
        var startX = width / 2f - mHouseBitmap!!.width * mTotalScaled / 2f
        var startY = height / 2f - mHouseBitmap!!.height * mTotalScaled / 2f
        while (index < mHouseDetail!!.mData!!.size) {
            val locator = mHouseDetail!!.mData!![index]
            //todo 有负数
            Log.d("ryq", "drawHouseDetail   index=" + index + " locator.mLocationActual.x=" + locator.mLocationActual.x
                    + " locator.mLocationActual.y=" + locator.mLocationActual.y)
            val unit = mHouseDetail!!.mBitmapActualWidth / (mHouseBitmap!!.width * mTotalScaled)
            val curX = startX + mHouseBitmap!!.width * locator.mLocationActual.x * mTotalScaled + mTotalDx
            val curDisX = curX * unit
            val curY = startY + mHouseBitmap!!.height * locator.mLocationActual.y * mTotalScaled + mTotalDy
            val curDisY = curY * unit

            if (locator.mIsSelected || locator.mIsAdded) {
                var nextIndex = index + 1
                while (nextIndex < mHouseDetail!!.mData!!.size) {
                    val nextLocator = mHouseDetail!!.mData!![nextIndex]
                    if (nextLocator.mIsSelected || nextLocator.mIsAdded) {
                        val nextX = startX + mHouseBitmap!!.width * nextLocator.mLocationActual.x * mTotalScaled + mTotalDx
                        val nextDisX = nextX * unit
                        val nextY = startY + mHouseBitmap!!.height * nextLocator.mLocationActual.y * mTotalScaled + mTotalDy
                        val nextDisY = nextY * unit

                        val dis = sqrt(((curDisX - nextDisX) * (curDisX - nextDisX) + (curDisY - nextDisY) * (curDisY - nextDisY)).toDouble())
                        val pixDis = sqrt(((curX - nextX) * (curX - nextX) + (curY - nextY) * (curY - nextY)).toDouble())
                        canvas.drawLine(curX, curY, nextX, nextY, mLinePaint)
                        val path = Path()
                        path.moveTo(curX, curY)
                        path.lineTo(nextX, nextY)
                        Log.d("ryq", "drawHouseDetail  "
                                + " nextLocator.mLocationActual.x=" + nextLocator.mLocationActual.x
                                + " nextLocator.mLocationActual.y=" + nextLocator.mLocationActual.y
                                + "drawHouseDetail  dis=$dis")
                        val disStr: String = String.format("%.1f", dis)
                        canvas.drawTextOnPath(disStr + "m", path, (pixDis / 2).toFloat(), 0f, mTextPaint)
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
