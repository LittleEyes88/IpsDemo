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
import com.mercku.ipsdemo.util.MathUtil
import com.mercku.ipsdemo.view.BaseEditView.Companion.DEFAULT_DOT_RADIUS
import com.mercku.ipsdemo.view.BaseEditView.Companion.NONE_TOUCH
import java.io.File

import java.util.ArrayList

/**
 * Created by yanqiong.ran on 2019-07-19.
 */
class MyHouseView : BaseEditView {

    private var mHouseDetail: IpsHouse? = null
    private var mHouseBitmap: Bitmap? = null


    internal var mMoveX = 0f
    internal var mMoveY = 0f

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Log.d("ryq", " widthMeasureSpec=$widthMeasureSpec  heightMeasureSpec=$heightMeasureSpec")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    fun addDot() {
        val dot = Node()
        val startCenterX = 200 + BaseEditView.DEFAULT_DOT_RADIUS
        val startCenterY = 200 + BaseEditView.DEFAULT_DOT_RADIUS
        dot.id = System.currentTimeMillis().toString()
        dot.cx = startCenterX
        dot.cy = startCenterY
        mDotList.add(dot)
        postInvalidate()
    }

    fun removeDot() {
        if (mSelectedDotIndex == BaseEditView.NONE_TOUCH) {
            Toast.makeText(mContext, "请先选择一个节点", Toast.LENGTH_LONG).show()
        } else {
            mDotList.removeAt(mSelectedDotIndex)
            mSelectedDotIndex = BaseEditView.NONE_TOUCH
            mLastSeletedDotIndex = BaseEditView.NONE_TOUCH
            postInvalidate()
        }
    }

    fun setHouseDetail(ipsHouse: IpsHouse?) {
        ipsHouse?.let {
            mHouseDetail = ipsHouse
            Log.d(BaseEditView.TAG, "setHouseDetail mHouseDetail=" + mHouseDetail)
            postInvalidate()
        }

    }

    /* fun setImageBitmap(bitmap: Bitmap?) {
         mHouseBitmap = Bitmap.createBitmap(bitmap);
         Log.d(BaseEditView.TAG, "setImageBitmap mHouseBitmap=" + mHouseBitmap)
         postInvalidate()
     }*/

    override fun onDraw(canvas: Canvas) {
        /* Log.d(BaseEditView.TAG, "onDraw canvas getLeft=" + left +
                 " getX()=" + x + " getY=" + y +
                 " getScrollX()=" + scrollX + " getScrollY=" + scrollY +
                 " getTranslationX()=" + translationX +
                 " getWidth()=" + width + " getHeight()=" + height
                 + " getScaleX= " + scaleX + " getScaleY=" + scaleY)*/
        canvas.drawColor(Color.WHITE/*, PorterDuff.Mode.CLEAR*/)
        //跟下面的是一样的效果
        /*var clearPaint = Paint();
        clearPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(clearPaint);
        clearPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC));*/

        drawHouseDetail(canvas)
        canvas.save()
        drawBackground(canvas)
        canvas.restore()
        canvas.scale(mTotalScaled, mTotalScaled, mZoomMidPoint.x, mZoomMidPoint.y)


        //canvas.restore();

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
                mHouseBitmap = Bitmap.createBitmap(bitmap)
            }

            var imgMatrix = Matrix()
            var imgDx = width / 2.0f - mHouseBitmap!!.width / 2.0f
            var imgDy = height / 2.0f - mHouseBitmap!!.height / 2.0f
            imgMatrix.preTranslate(imgDx, imgDy)
            canvas.drawBitmap(mHouseBitmap, imgMatrix, paint)


            mHouseDetail?.mData?.let {
                for (locator in mHouseDetail!!.mData!!) {
                    if (locator.mLocation.x > 0 && locator.mLocation.y > 0) {
                        var temp = BitmapFactory.decodeResource(resources, R.drawable.ic_location)
                        var dotBitmp = Bitmap.createBitmap(temp);
                        var matrix = Matrix()
                        matrix.preTranslate(imgDx + mHouseBitmap!!.width * locator.mLocation.x, imgDy + mHouseBitmap!!.height * locator.mLocation.y)
                        canvas.drawBitmap(dotBitmp, matrix, paint)
                    }

                }
            }

        }


    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        /*     this.pivotX = (BaseEditView.BITMAP_WIDTH / 2).toFloat()
             this.pivotY = (BaseEditView.BITMAP_HEIGHT / 2).toFloat()

             setWillNotDraw(false)
             mScaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener())//设置手势缩放的监听*/
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(BaseEditView.TAG, "onTouchEvent   event.getAction() & MotionEvent.ACTION_MASK=" + (event.action and MotionEvent.ACTION_MASK) +
                " event.getX=" + event.x + " event.getY=" + event.y +
                " event.getRawX=" + event.rawX + " event.getRawY=" + event.rawY +
                " getScrollX=" + scrollX + " event.getScaleY=" + scaleY +
                " event.getActionMasked()=" + event.actionMasked)
        /*if (event.getPointerCount() > 1) {
            mScaleGestureDetector.onTouchEvent(event);
            return true;
        }*/


        mCurrentX = event.x
        mCurrentY = event.y

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mLastDownX = mCurrentX
                mLastDownY = mCurrentY
                mMoveX = 0f
                mMoveY = 0f
                mTouchMode = BaseEditView.SINGLE_TOUCH_MODE
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                //多点触控
                /*Log.d(BaseEditView.TAG, "onTouchEvent  ACTION_POINTER_DOWN " +
                        " event.getPointerCount()=" + event.pointerCount)*/
                mMultiTouchOrgDis = distance(event)
                if (mMultiTouchOrgDis > mZoomSlop) {
                    mZoomMidPoint = midPoint(event)
                    mTouchMode = BaseEditView.MULTI_TOUCH_MODE_ZOOM
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // Log.d(BaseEditView.TAG, "ACTION_MOVE  mTouchMode=" + mTouchMode +
                //         "    mSelectedViewIndex =" + mSelectedViewIndex + "  mSelectedDotIndex = " + mSelectedDotIndex)
                // 是一个手指拖动

                mMoveX += Math.abs(mCurrentX - mLastDownX)//X轴距离
                mMoveY += Math.abs(mCurrentY - mLastDownY)//y轴距离
                if (mTouchMode == BaseEditView.SINGLE_TOUCH_MODE && (mMoveX > mTouchSlop || mMoveY > mTouchSlop)) {
                    // 没有房间或者打点选择，移动画布
                    // if (mSelectedViewIndex == BaseEditView.NONE_TOUCH && mSelectedDotIndex == BaseEditView.NONE_TOUCH) {
                    scrollTheCanvas()
                    // }
                } else if (mTouchMode == BaseEditView.MULTI_TOUCH_MODE_ZOOM) {
                    // 两个手指滑动
                    val newDist = distance(event)
                    if (newDist > 10f) {
                        val scale = newDist / mMultiTouchOrgDis
                        //抖动厉害

                        Log.d("ryq", " scale=" + scale +
                                " mTotalScaled=" + mTotalScaled
                                + " MyHouseView.this.getCameraDistance()=" + this@MyHouseView.cameraDistance)
                        if (mTotalScaled * scale >= 0.5) {
                            mTotalScaled *= scale
                            mScaled = scale
                            postInvalidate()
                            mMultiTouchOrgDis = newDist
                        }

                    }
                }
            }
            MotionEvent.ACTION_UP -> {

                // 手指放开事件
                mTouchMode = BaseEditView.TOUCH_MODE_NONE
                mIsScrollingX = false
                mIsScrollingY = false
                mScrolledX = 0f
                mScrolledY = 0f

            }
            MotionEvent.ACTION_CANCEL -> {
                // 手指放开事件
                mTouchMode = BaseEditView.TOUCH_MODE_NONE
                mIsScrollingX = false
                mIsScrollingY = false
                mScrolledX = 0f
                mScrolledY = 0f

            }
        }
        return true
    }

    /**
     * 计算两个手指头之间的中心点的位置
     * x = (x1+x2)/2;
     * y = (y1+y2)/2;
     *
     * @param event 触摸事件
     * @return 返回中心点的坐标
     */
    private fun midPoint(event: MotionEvent): PointF {
        if (event.pointerCount > 1) {
            val x = (event.getX(0) + event.getX(1)) / 2
            val y = (event.getY(0) + event.getY(1)) / 2
            return PointF(x, y)
        }
        return PointF()
    }


    /**
     * 计算两个手指间的距离
     *
     * @param event 触摸事件
     * @return 放回两个手指之间的距离
     */
    private fun distance(event: MotionEvent): Float {
        if (event.pointerCount > 1) {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return Math.sqrt((x * x + y * y).toDouble()).toFloat()//两点间距离公式
        }
        return 0f
    }

    private fun scrollTheCanvas() {
        var scrolledX = mLastDownX - mCurrentX
        var scrolledY = mLastDownY - mCurrentY
        Log.d(BaseEditView.TAG, "scrollTheCanvas  scrolledX=" + scrolledX +
                "    scrolledY =" + scrolledY
                + "  mIsScrollingX = " + mIsScrollingX
                + "  mIsScrollingY = " + mIsScrollingY)
        if (mIsScrollingX) {
            // scrollBy(scrolledX, 0)
            mScrolledX = scrolledX
            mLastDownX = mCurrentX
            postInvalidate()
        } else if (mIsScrollingY) {
            // scrollBy(0, scrolledY)
            mScrolledY = scrolledY
            mLastDownY = mCurrentY
            postInvalidate()
        } else if (Math.abs(scrolledX) >= Math.abs(scrolledY)) {
            if (Math.abs(scrolledX) >= mTouchSlop) {
                //   scrollBy(scrolledX, 0)
                mScrolledX = scrolledX
                mLastDownX = mCurrentX
                mIsScrollingX = true
                postInvalidate()
            }
        } else {
            if (Math.abs(scrolledY) >= mTouchSlop) {
                //  scrollBy(0, scrolledY)
                mScrolledY = scrolledY
                mLastDownY = mCurrentY
                mIsScrollingY = true
                postInvalidate()
            }
        }
    }


    private fun checkSelectedDot(): Int {
        var selectedViewIndex = BaseEditView.NONE_TOUCH
        for (index in mDotList.indices) {
            val node = mDotList[index]
            Log.d(BaseEditView.TAG, "checkSelectedView node.cx = " + node.cx
                    + " node.cy=" + node.cy
            )
            if (mCurrentX + scrollX >= node.cx - BaseEditView.DEFAULT_DOT_RADIUS - BaseEditView.NEAR && mCurrentX + scrollX <= node.cx + BaseEditView.DEFAULT_DOT_RADIUS + BaseEditView.NEAR
                    && mCurrentY + scrollY >= node.cy - BaseEditView.DEFAULT_DOT_RADIUS - BaseEditView.NEAR && mCurrentY + scrollY <= node.cy + BaseEditView.DEFAULT_DOT_RADIUS + BaseEditView.NEAR) {
                selectedViewIndex = index
                break
            }
        }
        return selectedViewIndex
    }

    private fun moveDot() {
        if (mLastSeletedDotIndex != BaseEditView.NONE_TOUCH) {
            if (mSelectedDotIndex != BaseEditView.NONE_TOUCH) {
                //move
                val node = mDotList[mSelectedDotIndex]
                node.cx = node.cx + mCurrentX - mLastDownX
                node.cy = node.cy + mCurrentY - mLastDownY
                postInvalidate()
            }
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
        super.computeScroll()
    }

    private fun smoothScrollTo(dx: Int, dy: Int) {
        mScroller.startScroll(mScroller.startX, mScroller.startY, dx, dy)
        postInvalidate()
    }


    private inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        // Focus point at the start of the pinch gesture. This is used for computing proper scroll
        // offsets during scaling, as well as for simultaneous panning.
        private var mStartFocusX: Float = 0.toFloat()
        private var mStartFocusY: Float = 0.toFloat()
        // View scale at the beginning of the gesture. This is used for computing proper scroll
        // offsets during scaling.
        private var mStartScale: Float = 0.toFloat()
        // View scroll offsets at the beginning of the gesture. These provide the reference point
        // for adjusting scroll in response to scaling and panning.
        private var mStartScrollX: Int = 0
        private var mStartScrollY: Int = 0

        /**
         * 缩放手势开始，当两个手指放在屏幕上的时候会调用该方法(只调用一次)。
         * 如果返回 false 则表示不使用当前这次缩放手势； 返回true则会进入onScale()函数
         *
         * @param detector
         * @return
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mStartFocusX = detector.focusX // 缩放中心，x坐标
            mStartFocusY = detector.focusY  // 缩放中心y坐标
            mStartScrollX = scrollX
            mStartScrollY = scrollY
            Log.d(BaseEditView.TAG, "onScaleBegin mViewScale = $mViewScale")
            mStartScale = mViewScale
            return true
        }

        /**
         * 缩放被触发(会调用0次或者多次)。
         * 如果返回 true 则表示当前缩放事件已经被处理，检测器会重新积累缩放因子；
         * 返回 false 则会继续积累缩放因子。
         *
         * @param detector
         * @return
         */

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val oldViewScale = mViewScale

            val scaleFactor = detector.scaleFactor// 缩放因子
            mStartScrollY = scrollY
            Log.d(BaseEditView.TAG, "onScale scaleFactor = $scaleFactor")
            mViewScale *= scaleFactor

            if (mViewScale < BaseEditView.ZOOM_SCALES[0]) {
                mCurrentZoomScaleIndex = 0
                mViewScale = BaseEditView.ZOOM_SCALES[mCurrentZoomScaleIndex]
            } else if (mViewScale > BaseEditView.ZOOM_SCALES[BaseEditView.ZOOM_SCALES.size - 1]) {
                mCurrentZoomScaleIndex = BaseEditView.ZOOM_SCALES.size - 1
                mViewScale = BaseEditView.ZOOM_SCALES[mCurrentZoomScaleIndex]
            } else {
                // find nearest zoom scale
                var minDist = java.lang.Float.MAX_VALUE
                // If we reach the end the last one was the closest
                var index = BaseEditView.ZOOM_SCALES.size - 1
                for (i in BaseEditView.ZOOM_SCALES.indices) {
                    val dist = Math.abs(mViewScale - BaseEditView.ZOOM_SCALES[i])
                    if (dist < minDist) {
                        minDist = dist
                    } else {
                        // When it starts increasing again we've found the closest
                        index = i - 1
                        break
                    }
                }
                mCurrentZoomScaleIndex = index
                // mViewScale = ZOOM_SCALES[mCurrentZoomScaleIndex];

            }

            /*  if (shouldDrawGrid()) {
                mGridRenderer.updateGridBitmap(mViewScale);
            }*/


            this@MyHouseView.scaleX = mViewScale
            this@MyHouseView.scaleY = mViewScale

            // Compute scroll offsets based on difference between original and new scaling factor
            // and the focus point where the gesture started. This makes sure that the scroll offset
            // is adjusted to keep the focus point in place on the screen unless there is also a
            // focus point shift (see next scroll component below).
            val scaleDifference = mViewScale - mStartScale
            val scrollScaleX = (scaleDifference * mStartFocusX).toInt()
            val scrollScaleY = (scaleDifference * mStartFocusY).toInt()

            // Compute scroll offset based on shift of the focus point. This makes sure the view
            // pans along with the focus.
            val scrollPanX = (mStartFocusX - detector.focusX).toInt()
            val scrollPanY = (mStartFocusY - detector.focusY).toInt()

            // Apply the computed scroll components for scale and panning relative to the scroll
            // coordinates at the beginning of the gesture.
            // scrollTo(mStartScrollX + scrollScaleX + scrollPanX,
            //         mStartScrollY + scrollScaleY + scrollPanY);
            Log.d(BaseEditView.TAG, "onScale mViewScale = " + mViewScale +
                    " mStartScale=" + mStartScale + " mStartFocusX=" + mStartFocusX
                    + " mStartFocusY=" + mStartFocusY +
                    " detector.getFocusX()=" + detector.focusX + "  detector.getFocusY()=" + detector.focusY)
            mIsScale = true
            return true
        }
    }


}
