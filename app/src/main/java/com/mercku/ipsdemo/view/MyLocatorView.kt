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
    private var mDotBitmap: Bitmap? = null


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }


    fun setHouseDetail(ipsHouse: IpsHouse?) {
        ipsHouse?.let {
            mHouseDetail = ipsHouse
            Log.d(TAG, "setHouseDetail mHouseDetail=$mHouseDetail")
            postInvalidate()
        }

    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        drawHouseDetail(canvas)
    }

    private fun drawHouseDetail(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true

        Log.d(TAG, "drawHouseDetail canvas mHouseBitmap=$mHouseBitmap")
        if (mHouseDetail == null || TextUtils.isEmpty(mHouseDetail!!.mImageFilePath)) {
            return
        }
        if (mHouseBitmap == null) {
            val file = File(mHouseDetail!!.mImageFilePath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                val bitmap = BitmapFactory.decodeStream(
                        mContext.contentResolver.openInputStream(uri))
                mHouseBitmap = BitmapUtil.resizeBitmap(bitmap, width, height)
            }
        }

        val imgMatrix = Matrix()
        val imgDx = width / 2.0f - mHouseBitmap!!.width / 2.0f
        val imgDy = height / 3.0f - mHouseBitmap!!.height / 2.0f
        imgMatrix.preTranslate(imgDx, imgDy)
        canvas.drawBitmap(mHouseBitmap!!, imgMatrix, paint)

        if (mHouseDetail?.mData == null) {
            return
        }
        var index = 0
        if (mDotBitmap == null) {
            val temp = BitmapFactory.decodeResource(resources, R.drawable.ic_location)
            mDotBitmap = Bitmap.createBitmap(temp)
        }
        while (index < mHouseDetail!!.mData!!.size) {
            val locator = mHouseDetail!!.mData!![index]
            if (locator.mIsSelected || locator.mIsAdded) {
                val matrix = Matrix()
                val transx = imgDx + mHouseBitmap!!.width * locator.mLocationActual.x - mDotBitmap!!.width / 2
                val transy = imgDy + mHouseBitmap!!.height * locator.mLocationActual.y - mDotBitmap!!.height / 2

                matrix.preTranslate(transx, transy)
                canvas.drawBitmap(mDotBitmap!!, matrix, paint)
            }
            index++
        }
        index = 0
        while (index < mHouseDetail!!.mData!!.size) {
            val locator = mHouseDetail!!.mData!![index]
            //todo 有负数
            Log.d("ryq", "drawHouseDetail   index=" + index + " locator.mLocationActual.x=" + locator.mLocationActual.x
                    + " locator.mLocationActual.y=" + locator.mLocationActual.y)
            val unit = mHouseDetail!!.mBitmapActualWidth / mHouseBitmap!!.width
            val curX = (imgDx + mHouseBitmap!!.width * locator.mLocationActual.x)
            val curDisX = curX * unit
            val curY = (imgDy + mHouseBitmap!!.height * locator.mLocationActual.y)
            val curDisY = curY * unit

            if (locator.mIsSelected || locator.mIsAdded) {
                var nextIndex = index + 1
                while (nextIndex < mHouseDetail!!.mData!!.size) {
                    val nextLocator = mHouseDetail!!.mData!![nextIndex]
                    Log.d("ryq", "drawHouseDetail  "
                            + " nextLocator.mLocationActual.x=" + nextLocator.mLocationActual.x
                            + " nextLocator.mLocationActual.y=" + nextLocator.mLocationActual.y)
                    if (nextLocator.mIsSelected || nextLocator.mIsAdded) {
                        val nextX = (imgDx + mHouseBitmap!!.width * nextLocator.mLocationActual.x)
                        val nextDisX = nextX * unit
                        val nextY = (imgDy + mHouseBitmap!!.height * nextLocator.mLocationActual.y)
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
