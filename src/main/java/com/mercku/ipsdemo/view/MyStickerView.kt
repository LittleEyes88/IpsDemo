package com.mercku.ipsdemo.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Region
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.EditText
import android.widget.Toast


import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.model.FreeRoom
import com.mercku.ipsdemo.model.IntersectionArea
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.model.Node
import com.mercku.ipsdemo.util.BitmapUtil
import com.mercku.ipsdemo.util.MathUtil
import com.mercku.ipsdemo.view.BaseEditView.Companion.DEFAULT_DOT_RADIUS
import com.mercku.ipsdemo.view.BaseEditView.Companion.NONE_TOUCH
import java.io.File

import java.util.ArrayList

/**
 * Created by yanqiong.ran on 2019-07-19.
 */
class MyStickerView : BaseEditView {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }


    fun setHouseDetail(ipsHouse: IpsHouse?) {
        ipsHouse?.let {
            mHouseDetail = ipsHouse
            Log.d(BaseEditView.TAG, "setHouseDetail mHouseDetail=" + mHouseDetail)
            postInvalidate()
        }

    }


    override fun onDraw(canvas: Canvas) {

        canvas.drawColor(Color.WHITE)

        drawHouseDetail(canvas)

    }

    private fun drawHouseDetail(canvas: Canvas) {
        var paint = Paint()
        paint.isAntiAlias = true

        Log.d(BaseEditView.TAG, "drawHouseDetail canvas mHouseBitmap=" + mHouseBitmap)

        mHouseDetail?.mImageFilePath?.let {
            var file = File(mHouseDetail!!.mImageFilePath)
            if (file.exists()) {
                var uri = Uri.fromFile(file)
                var bitmap = BitmapFactory.decodeStream(
                        mContext.getContentResolver().openInputStream(uri))
                android.util.Log.d("ryq", "drawHouseDetail  bitmap=" + bitmap)
                mHouseBitmap = BitmapUtil.resizeBitmap(bitmap, width, height)
            }

            var imgMatrix = Matrix()
            var imgDx = width / 2.0f - mHouseBitmap!!.width / 2.0f
            var imgDy = height / 3.0f - mHouseBitmap!!.height / 2.0f
            imgMatrix.preTranslate(imgDx, imgDy)
            canvas.drawBitmap(mHouseBitmap, imgMatrix, paint)


            mHouseDetail?.mData?.let {
                var index = 0
                var temp = BitmapFactory.decodeResource(resources, R.drawable.ic_location)
                var dotBitmp = Bitmap.createBitmap(temp);
                var points = ArrayList<PointF>(mHouseDetail!!.mData!!.size)
                //draw locators
                while (index < mHouseDetail!!.mData!!.size) {
                    var locator = mHouseDetail!!.mData!![index]
                    if (locator.mIsSelected || locator.mIsAdded) {

                        var matrix = Matrix()
                        var transx = imgDx + mHouseBitmap!!.width * locator.mLocationActual.x
                        var transy = imgDy + mHouseBitmap!!.height * locator.mLocationActual.y

                        var point = PointF(transx, transy)
                        points.add(point)
                        matrix.preTranslate(point.x - dotBitmp.width / 2, point.y - dotBitmp.height / 2)
                        canvas.drawBitmap(dotBitmp, matrix, paint)
                    }
                    index++

                }
                //get sticker's location
                var center = MathUtil.getPolygenCenter(points)
                var unit = mHouseDetail!!.mBitmapActualWidth / mHouseBitmap!!.width
                var curX = width / 2.0f
                var curDisX = curX * unit
                var curY = center.cy
                var curDisY = curY * unit

                //draw lines from dot to sticker
                index = 0
                while (index < mHouseDetail!!.mData!!.size) {
                    var locator = mHouseDetail!!.mData!![index]
                    if (locator.mIsSelected || locator.mIsAdded) {
                        var nextX = (imgDx + mHouseBitmap!!.width * locator.mLocationActual.x)
                        var nextDisX = nextX * unit
                        var nextY = (imgDy + mHouseBitmap!!.height * locator.mLocationActual.y)
                        var nextDisY = nextY * unit
                        android.util.Log.d("ryq", "drawHouseDetail2  mHouseDetail!!.mBitmapActualWidth=" + mHouseDetail!!.mBitmapActualWidth)
                        var actualDis = Math.sqrt(((curDisX - nextDisX) * (curDisX - nextDisX) + (curDisY - nextDisY) * (curDisY - nextDisY)).toDouble())
                        var pixDis = Math.sqrt(((curX - nextX) * (curX - nextX) + (curY - nextY) * (curY - nextY)).toDouble())
                        canvas.drawLine(curX, curY, nextX, nextY, mLinePaint)
                        var path = Path()
                        path.moveTo(curX, curY)
                        path.lineTo(nextX, nextY)
                        //距离有问题，可能因为坐标错误
                        android.util.Log.d("ryq", "drawHouseDetail2  dis=" + actualDis + " pixDis=" + pixDis)
                        var disStr: String = String.format("%.1f", actualDis)
                        canvas.drawTextOnPath(disStr + "m", path, (pixDis / 2).toFloat(), 0f, mTextPaint)
                    }
                    index++

                }
                //draw sticker
                var stickerTemp = BitmapFactory.decodeResource(resources, R.drawable.ic_sticker)
                var stickerBitmp = Bitmap.createBitmap(stickerTemp);
                var matrix = Matrix()
                matrix.preTranslate(curX - stickerBitmp.width / 2 /*+ temp.width / 2*/, curY - stickerBitmp.height / 2 /*+ temp.height / 2*/)
                canvas.drawBitmap(stickerBitmp, matrix, paint)

            }

        }


    }


}
