package com.mercku.ipsdemo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Scroller

import com.mercku.ipsdemo.model.FreeRoom
import com.mercku.ipsdemo.model.Node
import com.mercku.ipsdemo.model.RectRoom
import com.mercku.ipsdemo.MyMatrix
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.util.MathUtil

import java.util.AbstractList
import java.util.ArrayList

/**
 * Created by yanqiong.ran on 2019-08-01.
 */
open class BaseEditView : View {
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
    protected var mHouseList: ArrayList<RectRoom>? = ArrayList()
    var freeRoomData = ArrayList<FreeRoom>()
        protected set
    protected var mNameArrays = ArrayList<String>()
    protected lateinit var mHousePaint: Paint
    protected var mCurrentNEAR: Int = 0

    protected var mSelectedViewIndex: Int = 0
    protected lateinit var mTextPaint: Paint
    protected lateinit var mFocusedHousePaint: Paint
    protected lateinit var mFocusedSidePaint: Paint
    protected lateinit var mLinePaint: Paint
    protected var mLastSeletedViewIndex: Int = 0
    protected lateinit var mGridPaint: Paint
    protected var mScaleGestureDetector: ScaleGestureDetector? = null
    protected var mIsScale: Boolean = false

    protected var mCurrentMode: Int = 0
    protected var mDotList: AbstractList<Node> = ArrayList()
    protected var mSelectedDotIndex = -1
    protected var mLastSeletedDotIndex = -1

    protected var mTouchMode = SINGLE_TOUCH_MODE
    protected var mZoomMidPoint = PointF(0f, 0f)
    protected var mMultiTouchOrgDis: Float = 0.toFloat()
    protected var mTotalScaled = 1f
    protected var mScaled = 0f
    protected lateinit var mWallCornerPaint: Paint

    protected val TOUCH_MODE_NONE = 200
    protected val DEFAULT_DOT_RADIUS = 20f
    protected val DEFAULT_CORNER_RADIUS = 20f


    protected var mHouseDetail: IpsHouse? = null
    protected var mHouseBitmap: Bitmap? = null

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
        mTextPaint.color = Color.BLACK
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.isAntiAlias = true//用于防止边缘的锯齿
        mTextPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.mercku_text_size_h14).toFloat()
        mTextPaint.alpha = 1000//设置透明度



        mLinePaint = Paint(/*Paint.ANTI_ALIAS_FLAG*/);
        mLinePaint.setStyle(Paint.Style.STROKE);//画线条，线条有宽度
        mLinePaint.setColor(getResources().getColor(R.color.bg_grid_red));
        mLinePaint.setStrokeWidth(3f);//线条宽度
        mLinePaint.setPathEffect(DashPathEffect(floatArrayOf(5.0f, 6.0f), 0f));//线的显示效果：破折号格式


        mGridPaint = Paint()
        mGridPaint.color = resources.getColor(R.color.bg_grid_red)
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

    fun setMode(mode: Int) {
        mCurrentMode = mode
        mSelectedDotIndex = NONE_TOUCH
        mLastSeletedDotIndex = NONE_TOUCH
        mSelectedViewIndex = NONE_TOUCH
        mLastSeletedViewIndex = NONE_TOUCH
        postInvalidate()
    }


    protected fun drawEverySideLength(canvas: Canvas, room: FreeRoom) {
        val points = room.dotList
        if (points == null || points.size < 2) {
            return
        }
        for (index in 0 until points.size - 1) {
            val curNode = points[index]
            val nextNode = points[index + 1]
            val path = Path()
            path.moveTo(curNode.x, curNode.y)
            path.lineTo(nextNode.x, nextNode.y)
            val dis = MathUtil.distance(curNode.x, curNode.y,
                    nextNode.x, nextNode.y)
            val length = dis / DEFAULT_PIX_INTERVAL * MESH_ROW_DISTANCE
            val text = String.format("%.1f", length)
            val textPaintWidth = mTextPaint.measureText(text)
            canvas.drawTextOnPath(text, path, dis / 2 - textPaintWidth, -WALL_WIDTH, mTextPaint)
        }
    }


    protected fun drawRectRoomArea(canvas: Canvas, rect: RectF) {
        val width = (rect.right - rect.left) / DEFAULT_PIX_INTERVAL * MESH_ROW_DISTANCE
        val height = (rect.bottom - rect.top) / DEFAULT_PIX_INTERVAL * MESH_COL_DISTANCE
        val str = String.format("s=%.1f m²", width * height)
        val textPaintWidth = mTextPaint.measureText(str)
        val fontMetrics = mTextPaint.fontMetrics


        //show the area
        val textStartX = (rect.right - rect.left) / 2 + rect.left - textPaintWidth / 2
        val textStartY = (rect.bottom - rect.top) / 2 + rect.top + fontMetrics.bottom - fontMetrics.top
        canvas.drawText(str, textStartX, textStartY, mTextPaint)
        //show the width
        val strWidth = String.format("w=%.1f", width)
        canvas.drawText(strWidth, textStartX, rect.top + WALL_WIDTH, mTextPaint)
        //show the Height
        val strHeight = String.format("h=%.1f", height)
        val strHeightWidth = mTextPaint.measureText(strHeight)
        val path = Path()
        path.moveTo(rect.right - WALL_WIDTH, rect.top + (rect.bottom - rect.top - strHeightWidth) / 2)
        path.lineTo(rect.right - WALL_WIDTH, rect.bottom - (rect.bottom - rect.top - strHeightWidth) / 2)
        canvas.drawTextOnPath(strHeight, path, 0f, 0f, mTextPaint)
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
        //android.util.Log.d("ryq", " bitmap1 getTranslationX=" + getTranslationX() + " getTranslationY=" + getTranslationY()
        //        + " BITMAP_WIDTH=" + BITMAP_WIDTH + " BITMAP_HEIGHT=" + BITMAP_HEIGHT);
        android.util.Log.d("ryq", "drawBackground  mScaled=$mScaled")
        android.util.Log.d("ryq", "drawBackground bitmap1 getWidth()=" + bitmap.width + " bitmap getHeight()=" + bitmap.height
                + "  getScrollX()=" + scrollX + " getScrollY()=" + scrollY
                + " bitmap1 getTranslationX=" + translationX + " getTranslationY=" + translationY)

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
    }
}
