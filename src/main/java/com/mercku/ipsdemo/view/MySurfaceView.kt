package com.mercku.ipsdemo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.mercku.ipsdemo.MyMatrix
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.listener.OnViewTouchListener
import java.lang.ref.WeakReference


/**
 * Created by yanqiong.ran on 2019-08-31.
 */
class MySurfaceView : SurfaceView, SurfaceHolder.Callback {

    var mOnViewTouchListener: OnViewTouchListener? = null
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if (null != mDrawThread) {
            mDrawThread!!.stopThread();
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (null == mDrawThread) {
            mDrawThread = DrawThread(this@MySurfaceView)
            mDrawThread!!.start()
        }
    }

    private var mHolder: SurfaceHolder? = null
    private var mDrawThread: DrawThread? = null
    private lateinit var mGridPaint: Paint
    private lateinit var mContext: Context

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {

        mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.STROKE);//画线条，线条有宽度
        mGridPaint.setColor(getResources().getColor(R.color.bg_grid_red));
        mGridPaint.setStrokeWidth(1f);//线条宽度
        mGridPaint.setPathEffect(DashPathEffect(floatArrayOf(6.0f, 6.0f), 0f));//线的显示效果：破折号格式

        mContext = context
        mHolder = holder
        holder.addCallback(this)
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT);

        //setZOrderMediaOverlay(true)// 如已绘制SurfaceView则在surfaceView上一层绘制。
    }

    protected fun drawBackground(canvas: Canvas): Canvas {
        /**
         * 注意多个createBiamap重载函数，必须是可变位图对应的重载才能绘制
         * bitmap: 原图像
         * pixInterval: 网格线的横竖间隔，单位:像素
         */
        val pixInterval = BaseEditView.DEFAULT_PIX_INTERVAL
        BaseEditView.BITMAP_WIDTH = mContext.resources.displayMetrics.widthPixels * 2
        BaseEditView.BITMAP_HEIGHT = mContext.resources.displayMetrics.heightPixels * 2
        val bitmap = Bitmap.createBitmap(BaseEditView.BITMAP_WIDTH, BaseEditView.BITMAP_HEIGHT, Bitmap.Config.ARGB_8888)  //很重要

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
        android.util.Log.d("ryq", " bitmap1 getTranslationX=" + getTranslationX()
                + " getTranslationY=" + getTranslationY()
                + " getTotalDy=" + mOnViewTouchListener?.getTotalDy()
                + " getTotalDx=" + mOnViewTouchListener?.getTotalDx()
                + " mOnViewTouchListener?.getTotalScaled()=" + mOnViewTouchListener?.getTotalScaled()
                + " bitmap.width=" + bitmap.width + " bitmap.height=" + bitmap.height);
        android.util.Log.d("ryq", "drawBackground  scrollX=$scrollX" + scrollX + " scaleY=" + scaleY + " scaleX=" + scaleX)

        val myMatrix = MyMatrix.translationMatrix(-bitmap.width / 2.0f + scrollX, -bitmap.height / 2.0f + scrollY)
        canvas.drawBitmap(bitmap, myMatrix, paintBmp)
        return canvas
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mOnViewTouchListener?.let {
            if (event != null) {
                return mOnViewTouchListener!!.onTouch(this, event)
            }
        }
        return super.onTouchEvent(event)
    }

    private class DrawThread : Thread {

        private var isRunning = false;
        private var mReference: WeakReference<MySurfaceView>

        constructor(reference: MySurfaceView) {
            isRunning = true;
            mReference = WeakReference<MySurfaceView>(reference)

        }

        fun stopThread() {
            isRunning = false
            var workIsNotFinish = true
            while (workIsNotFinish) {
                try {
                    this.join()// 保证run方法执行完毕
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                workIsNotFinish = false
            }

        }

        public override fun run() {
            while (isRunning) {
                var canvas: Canvas? = null
                try {
                    mReference.get()?.mHolder?.let {
                        synchronized(it) {
                            canvas = mReference.get()!!.mHolder!!.lockCanvas();
                            if (canvas == null)

                                return
                            canvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                            mReference.get()!!.drawBackground(canvas!!)
                            mReference.get()!!.mHolder!!.unlockCanvasAndPost(canvas)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }
    }

}