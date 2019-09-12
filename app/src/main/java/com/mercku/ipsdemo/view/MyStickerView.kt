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
import android.text.TextUtils
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

    override fun onDraw(canvas: Canvas) {

        canvas.drawColor(Color.WHITE)

        drawHouseDetail(canvas)

    }

    private fun drawHouseDetail(canvas: Canvas) {

        drawHouseBitmap(canvas)
        var points = drawAllLocator(canvas)

        //get sticker's location
        var center = MathUtil.getPolygenCenter(points)
        var curX = width / 2.0f
        var curY = height / 2.0f

        var stickerPoint = PointF(curX, curY)
        drawLineWithSticker(stickerPoint, canvas)
        drawSticker(stickerPoint, canvas)

    }

    private fun drawSticker(stickerPoint: PointF, canvas: Canvas) {
        var stickerTemp = BitmapFactory.decodeResource(resources, R.drawable.ic_sticker)
        var stickerBitmp = Bitmap.createBitmap(stickerTemp);
        var matrix = Matrix()
        matrix.preTranslate(stickerPoint.x - stickerBitmp.width / 2, stickerPoint.y - stickerBitmp.height / 2)
        var paint = Paint()
        paint.isAntiAlias = true
        canvas.drawBitmap(stickerBitmp, matrix, paint)
    }

    private fun drawLineWithSticker(curPoint: PointF, canvas: Canvas) {
        var index = 0
        var unit = mHouseDetail!!.mBitmapActualWidth / (mHouseBitmap!!.width * mTotalScaled)
        var curDisX = curPoint.x * unit
        var curDisY = curPoint.y * unit
        var startX = getImgLeftAfterTransOrScale()
        var startY = getImgTopAfterTransOrScale()
        while (index < mHouseDetail!!.mData!!.size) {
            var locator = mHouseDetail!!.mData!![index]
            if (locator.mIsSelected || locator.mIsAdded) {
                var nextX = startX + mHouseBitmap!!.width * locator.mLocationActual.x * mTotalScaled
                var nextDisX = nextX * unit
                var nextY = startY + mHouseBitmap!!.height * locator.mLocationActual.y * mTotalScaled
                var nextDisY = nextY * unit
                var actualDis = MathUtil.distance(curDisX, curDisY, nextDisX, nextDisY)
                var pixDis = MathUtil.distance(curPoint.x, curPoint.y, nextX, nextY)

                canvas.drawLine(curPoint.x, curPoint.y, nextX, nextY, mLinePaint)

                var path = Path()
                path.moveTo(curPoint.x, curPoint.y)
                path.lineTo(nextX, nextY)
                //距离有问题，可能因为坐标错误
                var disStr: String = String.format("%.1f", actualDis)
                canvas.drawTextOnPath(disStr + "m", path, (pixDis / 2).toFloat(), 0f, mTextPaint)
            }
            index++

        }
    }
    override fun getImgInitialLeft(): Float {
        var left = (width / 2.0f - mHouseBitmap!!.width / 2.0f)
        android.util.Log.d("ryq", " getImgInitialLeft left=" + left)
        return left
    }

    override fun getImgInitialTop(): Float {
        var top = (height / 2.0f - mHouseBitmap!!.height / 2.0f)
        android.util.Log.d("ryq", " getImgInitialTop top=" + top)
        return top
    }


    override fun getImgLeftAfterTransOrScale(): Float {
        var left = (width / 2f - mHouseBitmap!!.width * mTotalScaled / 2f) + mTotalDx
        android.util.Log.d("ryq", " getImgLeftAfterTransOrScale left=" + left)
        return left

    }

    override fun getImgTopAfterTransOrScale(): Float {
        var top = height / 2f - mHouseBitmap!!.height * mTotalScaled / 2f + mTotalDy
        android.util.Log.d("ryq", " getImgLeftAfterTransOrScale top=" + top)
        return top

    }

}
