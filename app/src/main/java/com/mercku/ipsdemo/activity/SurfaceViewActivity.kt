package com.mercku.ipsdemo.activity

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.listener.OnViewTouchListener
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.util.BitmapUtil
import com.mercku.ipsdemo.util.CacheUtil
import com.mercku.ipsdemo.view.BaseEditView


/**
 * Created by yanqiong.ran on 2019-08-31.
 */
class SurfaceViewActivity : BaseContentActivity() {

    private var mImageTouchListener: OnViewTouchListener? = null
    private lateinit var mIpsHouse: IpsHouse
    private lateinit var mHouseImageView: ImageView
    private lateinit var mHouseLayout: ViewGroup
    private lateinit var mScaleImageView: ImageView
    private var mInitialWidth: Float = 0f
    private var mInitialHeight: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface_view)
        setRightTitleText(getString(R.string.save))

        mHouseLayout = findViewById(R.id.house_layout)
        mHouseImageView = findViewById(R.id.image_house)
        mImageTouchListener = OnViewTouchListener(mHouseImageView)
        mHouseImageView.setOnTouchListener(mImageTouchListener)
        mIpsHouse = intent.getParcelableExtra(ExtraConstants.EXTRA_HOUSE_DETAIL)
        val bitmap = BitmapFactory.decodeFile(mIpsHouse.mImageFilePath)
        mHouseImageView.viewTreeObserver.addOnGlobalLayoutListener {
            val scale = BitmapUtil.getScaleAfterResizeBitmap(bitmap, mHouseImageView.width, mHouseImageView.height)
            mInitialHeight = bitmap.height.toFloat() * scale
            mInitialWidth = bitmap.width * scale
        }
        mHouseImageView.setImageBitmap(bitmap)

        //transparent rect 48dp guide image 240dp
        mScaleImageView = findViewById(R.id.image_scale)
        mScaleImageView.post {
            //将图像中间扣成透明
            val scaleLayout = findViewById<View>(R.id.layout_scale)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val backgroundBitmap = Bitmap.createBitmap(scaleLayout.width, scaleLayout.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(backgroundBitmap)
            canvas.drawColor(ContextCompat.getColor(this, R.color.mercku_black_transparent))
            val transparentRectLength = mScaleImageView.width.div(240).times(48)
            paint.color = ContextCompat.getColor(this, R.color.mercku_transparent)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val scaleImageCenterX = mScaleImageView.left + mScaleImageView.width / 2f
            val scaleImageCenterY = mScaleImageView.top + mScaleImageView.width / 2f
            canvas.drawRect(scaleImageCenterX - transparentRectLength / 2, scaleImageCenterY - transparentRectLength / 2,
                    scaleImageCenterX + transparentRectLength / 2, scaleImageCenterY + transparentRectLength / 2, paint)
            scaleLayout.background = BitmapDrawable(resources, backgroundBitmap)
        }
    }

    override fun onClickRightTitleView() {
        mIpsHouse.mBitmapActualWidth = getActualBitmapWidth()
        CacheUtil.updateSomeHouse(mIpsHouse, this)
        val intent = Intent(this, HouseLayoutDetailActivity::class.java)
        intent.putExtra(ExtraConstants.EXTRA_IS_SAVE, true)
        intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, mIpsHouse)
        startActivity(intent)
    }

    private fun getActualBitmapWidth(): Float {
        return mInitialWidth / (BaseEditView.DEFAULT_PIX_INTERVAL * mImageTouchListener!!.getTotalScaled()) * BaseEditView.DEFAULT_EVERY_GRID_WIDTH
    }
}