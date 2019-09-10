package com.mercku.ipsdemo.view


import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.util.BitmapUtil
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
        canvas.drawColor(Color.WHITE)
        drawHouseDetail(canvas)
    }

    private fun drawHouseDetail(canvas: Canvas) {

        var pointLeft = drawHouseBitmap(canvas)
        var imgDx = 0f
        var imgDy = 0f
        pointLeft?.let {
            imgDx = it.x
            imgDy = it.y
        }

        var points = drawAllLocator(imgDx, imgDy, canvas)
        drawLineBetweenAnyTwoPoints(imgDx, imgDy, canvas)
    }

    private fun drawLineBetweenAnyTwoPoints(startX: Float, startY: Float, canvas: Canvas) {
        var index = 0
        while (index < mHouseDetail!!.mData!!.size) {
            val locator = mHouseDetail!!.mData!![index]
            //todo 有负数
            Log.d("ryq", "drawHouseDetail   index=" + index + " locator.mLocationActual.x=" + locator.mLocationActual.x
                    + " locator.mLocationActual.y=" + locator.mLocationActual.y)
            val unit = mHouseDetail!!.mBitmapActualWidth / mHouseBitmap!!.width
            val curX = (startX + mHouseBitmap!!.width * locator.mLocationActual.x)
            val curDisX = curX * unit
            val curY = (startY + mHouseBitmap!!.height * locator.mLocationActual.y)
            val curDisY = curY * unit

            if (locator.mIsSelected || locator.mIsAdded) {
                var nextIndex = index + 1
                while (nextIndex < mHouseDetail!!.mData!!.size) {
                    val nextLocator = mHouseDetail!!.mData!![nextIndex]
                    Log.d("ryq", "drawHouseDetail  "
                            + " nextLocator.mLocationActual.x=" + nextLocator.mLocationActual.x
                            + " nextLocator.mLocationActual.y=" + nextLocator.mLocationActual.y)
                    if (nextLocator.mIsSelected || nextLocator.mIsAdded) {
                        val nextX = (startX + mHouseBitmap!!.width * nextLocator.mLocationActual.x)
                        val nextDisX = nextX * unit
                        val nextY = (startY + mHouseBitmap!!.height * nextLocator.mLocationActual.y)
                        val nextDisY = nextY * unit
                        Log.d("ryq", "drawHouseDetail  mHouseDetail!!.mBitmapActualWidth=" + mHouseDetail!!.mBitmapActualWidth)
                        val dis = sqrt(((curDisX - nextDisX) * (curDisX - nextDisX) + (curDisY - nextDisY) * (curDisY - nextDisY)).toDouble())
                        val pixDis = sqrt(((curX - nextX) * (curX - nextX) + (curY - nextY) * (curY - nextY)).toDouble())
                        canvas.drawLine(curX, curY, nextX, nextY, mLinePaint)
                        val path = Path()
                        path.moveTo(curX, curY)
                        path.lineTo(nextX, nextY)

                        Log.d("ryq", "drawHouseDetail  dis=$dis")
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
