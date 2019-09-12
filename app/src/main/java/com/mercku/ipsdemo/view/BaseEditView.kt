package com.mercku.ipsdemo.view

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import androidx.core.content.ContextCompat
import com.mercku.ipsdemo.MyMatrix
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.model.Node
import com.mercku.ipsdemo.util.BitmapUtil
import com.mercku.ipsdemo.util.MathUtil
import java.io.File
import java.util.*


/**
 * Created by yanqiong.ran on 2019-08-01.
 */
abstract class BaseEditView : View {
    private var mImgMatrix: Matrix? = null
    private var mLocatorMatrix: Matrix? = null
    protected var mScrolledY: Float = 0f
    protected var mScrolledX: Float = 0f
    protected lateinit var mScroller: Scroller
    protected lateinit var mContext: Context
    protected var mLastDownX: Float = 0.toFloat()
    protected var mLastDownY: Float = 0.toFloat()
    protected var mCurrentX: Float = 0.toFloat()
    protected var mCurrentY: Float = 0.toFloat()
    protected var mTouchSlop = 10f
    protected var mZoomSlop = 10f
    protected var mIsScrollingX: Boolean = false
    protected var mIsScrollingY: Boolean = false
    protected var mCurrentZoomScaleIndex = INIT_ZOOM_SCALES_INDEX
    protected var mViewScale = ZOOM_SCALES[INIT_ZOOM_SCALES_INDEX]
    protected var mNameArrays = ArrayList<String>()
    protected lateinit var mHousePaint: Paint

    protected var mSelectedViewIndex: Int = 0
    protected lateinit var mTextPaint: Paint
    protected lateinit var mFocusedHousePaint: Paint
    protected lateinit var mFocusedSidePaint: Paint
    protected lateinit var mLinePaint: Paint
    protected var mLastSeletedViewIndex: Int = 0
    protected lateinit var mGridPaint: Paint
    protected var mIsScale: Boolean = false

    protected var mCurrentMode: Int = 0
    protected var mDotList: AbstractList<Node> = ArrayList()
    protected var mSelectedDotIndex = -1
    protected var mLastSeletedDotIndex = -1

    protected var mTouchMode = SINGLE_TOUCH_MODE
    protected var mZoomMidPoint = PointF(0f, 0f)
    protected var mMultiTouchOrgDis: Float = 0.toFloat()
    protected var mTotalScaled = 1f
    protected var mCurrentScaled = 1f
    protected var mScaled = 0f
    protected lateinit var mWallCornerPaint: Paint

    protected val TOUCH_MODE_NONE = 200
    protected val DEFAULT_DOT_RADIUS = 20f
    protected val DEFAULT_CORNER_RADIUS = 20f

    protected var mHouseDetail: IpsHouse? = null
    protected var mHouseBitmap: Bitmap? = null
    protected var mDotBitmap: Bitmap? = null
    protected var mMatrixValues = FloatArray(9)
    private var mPointF = PointF()


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }


    protected fun init(context: Context) {
        mContext = context
        mScroller = Scroller(context)
        mNameArrays.add("客厅")
        mNameArrays.add("卧室")
        mNameArrays.add("厨房")
        mNameArrays.add("卫生间")
        mNameArrays.add("卧室")

        mHousePaint = Paint()
        mHousePaint.color = Color.GRAY
        mHousePaint.strokeWidth = WALL_WIDTH
        mHousePaint.style = Paint.Style.STROKE
        mHousePaint.isAntiAlias = true//用于防止边缘的锯齿
        mHousePaint.alpha = 1000//设置透明度

        mFocusedHousePaint = Paint()
        mFocusedHousePaint.color = Color.DKGRAY
        mFocusedHousePaint.strokeWidth = WALL_WIDTH
        mFocusedHousePaint.style = Paint.Style.STROKE
        mFocusedHousePaint.isAntiAlias = true//用于防止边缘的锯齿
        mFocusedHousePaint.alpha = 1000//设置透明度

        mFocusedSidePaint = Paint()
        mFocusedSidePaint.color = Color.BLUE
        mFocusedSidePaint.strokeWidth = WALL_WIDTH
        mFocusedSidePaint.style = Paint.Style.STROKE
        mFocusedSidePaint.isAntiAlias = true//用于防止边缘的锯齿
        mFocusedSidePaint.alpha = 1000//设置透明度

        mTextPaint = Paint()
        mTextPaint.color = Color.WHITE
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.isAntiAlias = true//用于防止边缘的锯齿
        mTextPaint.textSize = context.resources.getDimensionPixelSize(com.mercku.ipsdemo.R.dimen.mercku_text_size_h14).toFloat()
        mTextPaint.alpha = 1000//设置透明度
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD)
        mTextPaint.setShadowLayer(  15f ,0f,0f,ContextCompat.getColor(mContext, com.mercku.ipsdemo.R.color.bg_grid_red));


        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.setStyle(Paint.Style.STROKE)//画线条，线条有宽度
        mLinePaint.color = ContextCompat.getColor(mContext, com.mercku.ipsdemo.R.color.bg_grid_red)
        mLinePaint.setStrokeWidth(6f);//线条宽度
        mLinePaint.setPathEffect(DashPathEffect(floatArrayOf(16.0f, 10.0f), 0f));//线的显示效果：破折号格式


        mGridPaint = Paint()
        mGridPaint.color = ContextCompat.getColor(mContext, com.mercku.ipsdemo.R.color.bg_grid_red)
        mGridPaint.strokeJoin = Paint.Join.ROUND
        mGridPaint.strokeCap = Paint.Cap.ROUND
        mGridPaint.strokeWidth = 1f
        mGridPaint.style = Paint.Style.STROKE
        var pathEffect: DashPathEffect = DashPathEffect(floatArrayOf(3.0f, 2.0f), 1.0f);
        mGridPaint.pathEffect = pathEffect

        mWallCornerPaint = Paint()
        mWallCornerPaint.color = Color.LTGRAY
        mWallCornerPaint.style = Paint.Style.FILL
        mWallCornerPaint.isAntiAlias = true//用于防止边缘的锯齿
        mWallCornerPaint.alpha = 1000//设置透明度

    }

    fun setHouseDetail(ipsHouse: IpsHouse?) {
        ipsHouse?.let {
            mHouseDetail = ipsHouse
            Log.d(TAG, "setHouseDetail mHouseDetail=$mHouseDetail")
            postInvalidate()
        }

    }

    /**
     * 绘制户型图
     * 返回左上角的坐标
     */
    fun drawHouseBitmap(canvas: Canvas) {
        var paint = Paint()
        paint.isAntiAlias = true

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
        Log.d("ryq", " mHouseBitmap!!.width=" + mHouseBitmap!!.width + "  mHouseBitmap!!.height=" + mHouseBitmap!!.height
                + " width=" + width + " height=" + height)
        if (mImgMatrix == null) {
            mImgMatrix = Matrix()
            mImgMatrix?.preTranslate(getImgInitialLeft(), getImgInitialTop())
        } else if (MODE_DRAG == mMode) {
            /**只有在拖拉过程中用postTranslate,基于之前的位移操作进行变换,不会清除之前的位移。
             * setTranslate会清除之前的位移。postTranslate和preTranslate是基于之前的操作变化，不会清除之前的位移。
             * postTranslate： M' = T(dx, dy) * M
             * preTranslate：M' = M * T(dx, dy)
             *  且setTranslate会清除之前的位移和setScale不可同时使用
             */
            mImgMatrix?.postTranslate(mCurrentDx, mCurrentDy)
        } else if (MODE_ZOOM == mMode) {
            //只有在缩放过程中可以用postScale， 基于之前的缩放操作进行变换
            mImgMatrix?.postScale(mCurrentScaled, mCurrentScaled, mHouseBitmap!!.width / 2f, mHouseBitmap!!.height / 2f)
        }

        if (mHouseBitmap != null && mImgMatrix != null) {
            canvas.drawBitmap(mHouseBitmap!!, mImgMatrix!!, paint)
        }

    }

    abstract fun getImgInitialLeft(): Float

    abstract fun getImgInitialTop(): Float

    abstract fun getImgLeftAfterTransOrScale(): Float

    abstract fun getImgTopAfterTransOrScale(): Float

    /**
     * 此时pivotX=0.0 pivotY=0.0
     */
    /* override fun onFinishInflate() {
         super.onFinishInflate()
         pivotX = width / 2.0f
         pivotY = height / 2.0f
     }*/

    /**
     *
     */
    fun drawAllLocator(canvas: Canvas): ArrayList<PointF>? {
        var paint = Paint()
        paint.isAntiAlias = true
        if (mHouseDetail == null || mHouseDetail!!.mData == null) {
            return null
        }

        var index = 0
        if (mDotBitmap == null) {
            val temp = BitmapFactory.decodeResource(resources, com.mercku.ipsdemo.R.drawable.ic_location)
            mDotBitmap = Bitmap.createBitmap(temp)
        }
        var points = ArrayList<PointF>(mHouseDetail!!.mData!!.size)
        mImgMatrix!!.getValues(mMatrixValues)
        val scaleX = mMatrixValues[Matrix.MSCALE_X]
        val transX = mMatrixValues[Matrix.MTRANS_X]
        val scaleY = mMatrixValues[Matrix.MSCALE_Y]
        val transY = mMatrixValues[Matrix.MTRANS_Y]
        while (index < mHouseDetail!!.mData!!.size) {
            var locator = mHouseDetail!!.mData!![index]
            if (locator.mIsSelected || locator.mIsAdded) {
                val srcX = mHouseBitmap!!.width * locator.mLocationActual.x - mDotBitmap!!.width / 2
                val srcY = mHouseBitmap!!.height * locator.mLocationActual.y - mDotBitmap!!.height / 2
                // 变化后的点
                val x = srcX * scaleX + 1 * transX
                val y = srcY * scaleY + 1 * transY
                mDotBitmap?.let {
                    canvas.drawBitmap(it, x, y, paint)
                }
                val point = PointF()
                point.x = x + mDotBitmap!!.width / 2
                point.y = y + mDotBitmap!!.height / 2
                points.add(point)
            }
            index++
        }
        return points
    }

    /**
     * https://blog.csdn.net/guolin_blog/article/details/48719871
     *
     * @param canvas
     * @return
     */
    protected fun drawBackground(canvas: Canvas): Canvas {
        /**
         * 注意多个createBiamap重载函数，必须是可变位图对应的重载才能绘制
         * bitmap: 原图像
         * pixInterval: 网格线的横竖间隔，单位:像素
         */
        val pixInterval = (DEFAULT_PIX_INTERVAL * mTotalScaled).toInt()
        BITMAP_WIDTH = mContext.resources.displayMetrics.widthPixels * 2
        BITMAP_HEIGHT = mContext.resources.displayMetrics.heightPixels * 2
        val bitmap = Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Bitmap.Config.ARGB_8888)  //很重要
        //设置颜色后会覆盖掉图片
        // bitmap.eraseColor(Color.WHITE);//填充颜色
        //canvas.drawColor(Color.argb(0,0,0,0));

        val bimapCanvas = Canvas(bitmap)  //创建画布
        val paintBmp = Paint()  //画笔
        paintBmp.isAntiAlias = true //抗锯齿
        bimapCanvas.drawBitmap(bitmap, Matrix(), paintBmp)  //在画布上画一个和bitmap一模一样的图

        //根据Bitmap大小，画网格线
        //画横线
        for (i in 0 until bitmap.height / pixInterval) {
            bimapCanvas.drawLine(0f, (i * pixInterval).toFloat(), bitmap.width.toFloat(), (i * pixInterval).toFloat(), mGridPaint)
        }
        //画竖线
        for (i in 0 until bitmap.width / pixInterval) {
            bimapCanvas.drawLine((i * pixInterval).toFloat(), 0f, (i * pixInterval).toFloat(), bitmap.height.toFloat(), mGridPaint)
        }

        val myMatrix = MyMatrix.translationMatrix(-bitmap.width / 2 + mScrolledX, -bitmap.height / 2 + mScrolledY)
        canvas.drawBitmap(bitmap, myMatrix, paintBmp)
        return canvas
    }

    companion object {
        var BITMAP_WIDTH: Int = 0
        var BITMAP_HEIGHT: Int = 0
        val TOUCH_MODE_NONE = 200
        val SINGLE_TOUCH_MODE = 201
        val MULTI_TOUCH_MODE_ZOOM = 202
        // Scale and zoom in/out factor.
        val INIT_ZOOM_SCALES_INDEX = 0
        // private static final float[] ZOOM_SCALES = new float[]{0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 3.0f};
        val ZOOM_SCALES = floatArrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 3.0f)
        val MIN_SCALE = 0.5f
        val TAG = "ryq-ImageEditingView"
        val NEAR = 30f
        val MIN_MOVE_DIS = 6f
        val WALL_WIDTH = 30f
        val NONE_TOUCH = -1
        val DEFAULT_PIX_INTERVAL = 60
        val DEFAULT_EVERY_GRID_WIDTH = 1 //UNIT:m
        val DEFAULT_HOUSE_WIDTH = (DEFAULT_PIX_INTERVAL * 6).toFloat()//6个格子的宽度
        val DEFAULT_HOUSE_HEIGHT = (DEFAULT_PIX_INTERVAL * 6).toFloat()//6个格子的高度
        val DEFAULT_DOT_RADIUS = 20f
        val MESH_ROW_DISTANCE = 0.5f
        val MESH_COL_DISTANCE = 0.5f
        /**
         * 拖拉模式
         */
        private val MODE_DRAG = 1
        /**
         * 放大缩小模式
         */
        private val MODE_ZOOM = 2
    }

    /**
     * 记录是放大缩小模式还是拖拉模式
     */
    var mMode = 0// 初始状态
    /**
     * 用于记录开始时候的坐标位置
     */
    var mTotalDx = 0f
    var mTotalDy = 0f
    var mCurrentDx = 0f
    var mCurrentDy = 0f
    val mStartPoint = PointF()
    val mLastPoint = PointF()
    /**
     * 两个手指的开始距离
     */
    var mStartDis: Float = 0.toFloat()

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255  */
        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            // 手指压下屏幕
            MotionEvent.ACTION_DOWN -> {
                mMode = MODE_DRAG
                // 记录ImageView当前的移动位置
                //mCurrentMatrix.set(mImageView.imageMatrix)
                mStartPoint.set(event.x, event.y)
                mLastPoint.set(event.x, event.y)
            }
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            MotionEvent.ACTION_POINTER_DOWN -> {
                mMode = MODE_ZOOM
                /** 计算两个手指间的距离  */
                mStartDis = MathUtil.distance(event.getX(1), event.getY(1), event.getX(0), event.getY(0))// 结束距离
            }
            // 手指在屏幕上移动，改事件会被不断触发
            MotionEvent.ACTION_MOVE ->
                // 拖拉图片
                if (mMode == MODE_DRAG) {

                    //val dx = event.x - mStartPoint.x // 得到x轴的移动距离
                    //val dy = event.y - mStartPoint.y // 得到x轴的移动距离
                    // 在没有移动之前的位置上进行移动

                    mCurrentDx = event.x - mLastPoint.x
                    mCurrentDy = event.y - mLastPoint.y
                    mLastPoint.set(event.x, event.y)

                    mTotalDx += mCurrentDx
                    mTotalDy += mCurrentDy

                    invalidate()

                } else if (mMode == MODE_ZOOM) {
                    val endDis = MathUtil.distance(event.getX(1), event.getY(1), event.getX(0), event.getY(0))// 结束距离
                    if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        // val scale = endDis / mStartDis// 得到缩放倍数
                        mCurrentScaled = endDis / mStartDis
                        if (mTotalScaled * mCurrentScaled > 0.5 && mTotalScaled * mCurrentScaled < 5) {
                            mTotalScaled *= mCurrentScaled
                            mStartDis = endDis
                            invalidate()
                        }


                    }
                }// 放大缩小图片
            // 手指离开屏幕
            MotionEvent.ACTION_UP,
                // 当触点离开屏幕，但是屏幕上还有触点(手指)
            MotionEvent.ACTION_POINTER_UP -> mMode = 0
        }

        return true
    }

}
