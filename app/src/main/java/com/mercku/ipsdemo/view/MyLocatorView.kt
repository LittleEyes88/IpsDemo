package com.mercku.ipsdemo.view


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import com.mercku.ipsdemo.util.MathUtil
import java.util.*

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
        drawHouseBitmap(canvas)
        drawLineBetweenAnyTwoPoints(canvas, drawAllLocator(canvas))
    }

    private fun drawLineBetweenAnyTwoPoints(canvas: Canvas, positionList: ArrayList<PointF>?) {
        if (positionList.isNullOrEmpty()) {
            return
        }
        val unit = mHouseDetail!!.mBitmapActualWidth / (mHouseBitmap!!.width * mTotalScaled)
        for (index in 0 until positionList.size) {
            val curPoint = positionList[index]
            val curX = curPoint.x
            val curY = curPoint.y
            val curDisX = curPoint.x * unit
            val curDisY = curPoint.y * unit
            for (nextIndex in index + 1 until positionList.size) {
                val nextPoint = positionList[nextIndex]
                val nextX = nextPoint.x
                val nextY = nextPoint.y
                val nextDisX = nextPoint.x * unit
                val nextDisY = nextPoint.y * unit
                val actualDis = MathUtil.distance(curDisX, curDisY, nextDisX, nextDisY)
                val pixDis = MathUtil.distance(curX, curY, nextX, nextY)

                canvas.drawLine(curX, curY, nextX, nextY, mLinePaint)
                val path = Path()
                path.moveTo(curX, curY)
                path.lineTo(nextX, nextY)
                val disStr: String = String.format("%.1f", actualDis)
                canvas.drawTextOnPath(disStr + "m", path, pixDis / 2, 0f, mTextPaint)
            }
        }
//        var index = 0
//        var startX = getImgLeftAfterTransOrScale()
//        var startY = getImgTopAfterTransOrScale()
//        while (index < mHouseDetail!!.mData!!.size) {
//            val locator = mHouseDetail!!.mData!![index]
//
//            val curX = startX + mHouseBitmap!!.width * locator.mLocationActual.x * mTotalScaled
//            val curDisX = curX * unit
//            val curY = startY + mHouseBitmap!!.height * locator.mLocationActual.y * mTotalScaled
//            val curDisY = curY * unit
//
//            if (locator.mIsSelected || locator.mIsAdded) {
//                var nextIndex = index + 1
//                while (nextIndex < mHouseDetail!!.mData!!.size) {
//                    val nextLocator = mHouseDetail!!.mData!![nextIndex]
//                    if (nextLocator.mIsSelected || nextLocator.mIsAdded) {
//                        val nextX = startX + mHouseBitmap!!.width * nextLocator.mLocationActual.x * mTotalScaled
//                        val nextDisX = nextX * unit
//                        val nextY = startY + mHouseBitmap!!.height * nextLocator.mLocationActual.y * mTotalScaled
//                        val nextDisY = nextY * unit
//
//
//                    }
//                    nextIndex++
//                }
//            }
//            index++
//
//        }

    }

    override fun getImgInitialLeft(): Float {
        var left = (width / 2.0f - mHouseBitmap!!.width / 2.0f)
        android.util.Log.d("ryq", " getImgInitialLeft left=" + left)
        return left
    }

    override fun getImgInitialTop(): Float {
        var top = (height / 3.0f - mHouseBitmap!!.height / 2.0f)
        android.util.Log.d("ryq", " getImgInitialTop top=" + top)
        return top
    }

    override fun getImgLeftAfterTransOrScale(): Float {
        var left = (width / 2f - mHouseBitmap!!.width * mTotalScaled / 2f) + mTotalDx
        android.util.Log.d("ryq", " getImgLeftAfterTransOrScale left=" + left)
        return left

    }

    override fun getImgTopAfterTransOrScale(): Float {
        var top = height / 3f - mHouseBitmap!!.height * mTotalScaled / 2f + mTotalDy
        android.util.Log.d("ryq", " getImgLeftAfterTransOrScale top=" + top)
        return top

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHouseBitmap?.recycle()
        mDotBitmap?.recycle()
    }

}
