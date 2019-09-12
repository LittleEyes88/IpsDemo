package com.mercku.ipsdemo.view


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.util.MathUtil

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
        var center = MathUtil.getCenterOfPoint(points)
        var curX = center.x
        var curY = center.y

        var stickerPoint = PointF(curX, curY)
        drawLineWithSticker(stickerPoint, points, canvas)
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

    private fun drawLineWithSticker(curPoint: PointF, positionList: ArrayList<PointF>?, canvas: Canvas) {
        if (positionList.isNullOrEmpty()) {
            return
        }
        val unit = mHouseDetail!!.mBitmapActualWidth / (mHouseBitmap!!.width * mTotalScaled)
        val curX = curPoint.x
        val curY = curPoint.y
        val curDisX = curPoint.x * unit
        val curDisY = curPoint.y * unit
        for (nextIndex in 0 until positionList.size) {
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
//        var index = 0
//        var unit = mHouseDetail!!.mBitmapActualWidth / (mHouseBitmap!!.width * mTotalScaled)
//        var curDisX = curPoint.x * unit
//        var curDisY = curPoint.y * unit
//        var startX = getImgLeftAfterTransOrScale()
//        var startY = getImgTopAfterTransOrScale()
//        while (index < mHouseDetail!!.mData!!.size) {
//            var locator = mHouseDetail!!.mData!![index]
//            if (locator.mIsSelected || locator.mIsAdded) {
//                var nextX = startX + mHouseBitmap!!.width * locator.mLocationActual.x * mTotalScaled
//                var nextDisX = nextX * unit
//                var nextY = startY + mHouseBitmap!!.height * locator.mLocationActual.y * mTotalScaled
//                var nextDisY = nextY * unit
//                var actualDis = MathUtil.distance(curDisX, curDisY, nextDisX, nextDisY)
//                var pixDis = MathUtil.distance(curPoint.x, curPoint.y, nextX, nextY)
//
//                canvas.drawLine(curPoint.x, curPoint.y, nextX, nextY, mLinePaint)
//
//                var path = Path()
//                path.moveTo(curPoint.x, curPoint.y)
//                path.lineTo(nextX, nextY)
//                //距离有问题，可能因为坐标错误
//                var disStr: String = String.format("%.1f", actualDis)
//                canvas.drawTextOnPath(disStr + "m", path, (pixDis / 2).toFloat(), 0f, mTextPaint)
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
