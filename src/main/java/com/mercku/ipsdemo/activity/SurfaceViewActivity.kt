package com.mercku.ipsdemo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.view.MySurfaceView
import android.widget.ImageView
import android.widget.TextView
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.listener.OnViewTouchListener
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.model.IpsLocator
import com.mercku.ipsdemo.util.BitmapUtil
import com.mercku.ipsdemo.util.CacheUtil
import com.mercku.ipsdemo.view.BaseEditView
import java.io.File


/**
 * Created by yanqiong.ran on 2019-08-31.
 */
class SurfaceViewActivity : BaseContentActivity() {

    private lateinit var mSurfaceView: MySurfaceView
    private var mSurfaceTouchListener: OnViewTouchListener? = null
    private lateinit var mIpsHouse: IpsHouse
    private lateinit var mHintImageView: ImageView
    private lateinit var mHintTextView: TextView
    private lateinit var mHouseImageView: ImageView
    private lateinit var mHouseLayout: ViewGroup
    private var mBitmap: Bitmap? = null
    private var mInitialWidth: Float = 0f
    private var mInitialHeight: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface_view)
        setRightTitleText(getString(R.string.save))

        mSurfaceView = findViewById(R.id.layout_surface_view)
        mSurfaceTouchListener = OnViewTouchListener(mSurfaceView)
        mSurfaceView.mOnViewTouchListener = mSurfaceTouchListener
        mIpsHouse = intent.getParcelableExtra<IpsHouse>(ExtraConstants.EXTRA_HOUSE_DETAIL)
        if (mIpsHouse == null)
            return
        mHouseLayout = findViewById(R.id.house_layout)
        mHouseImageView = findViewById<ImageView>(R.id.image_house)
        initHouseLayout(mIpsHouse)
        mHintImageView = findViewById(R.id.img_hint)
        mHintImageView.setOnClickListener {
            mHintTextView.visibility = if (mHintTextView.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        mHintTextView = findViewById(R.id.text_hint)
    }

    override fun onClickRightTitleView() {
        mIpsHouse.mBitmapActualWidth = getActualBitmapWidth()
        CacheUtil.updateSomeHouse(mIpsHouse, this)
        var intent = Intent(this, HouseLayoutDetailActivity::class.java)
        intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, mIpsHouse)
        startActivity(intent)
    }

    private fun getActualBitmapWidth(): Float {
        return mInitialWidth / (BaseEditView.DEFAULT_PIX_INTERVAL * mSurfaceTouchListener!!.getTotalScaled()) * BaseEditView.DEFAULT_EVERY_GRID_WIDTH

    }

    private fun initHouseLayout(ipsHouse: IpsHouse) {
        android.util.Log.d("ryq", "SurfaceViewActivity  ipsHouse.mImageFilePath=" + ipsHouse.mImageFilePath)
        var file = File(ipsHouse.mImageFilePath)
        if (file.exists()) {
            var uri = Uri.fromFile(file)
            mBitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri))
            android.util.Log.d("ryq", "SurfaceViewActivity mBitmap!!.width=" + mBitmap!!.width + "  mBitmap!!.height=" + mBitmap!!.height)
            mHouseImageView.setImageBitmap(mBitmap)
        }

        mHouseImageView.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                mBitmap?.let {
                    var scale = BitmapUtil.getScaleAfterResizeBitmap(mBitmap!!, mHouseImageView.width, mHouseImageView.height)
                    mInitialHeight = mBitmap!!.height.toFloat() * scale
                    mInitialWidth = mBitmap!!.width * scale
                }

                android.util.Log.d("ryq", "SurfaceViewActivity mInitialWidth=" + mInitialWidth
                        + " mInitialHeight=" + mInitialHeight
                        + " mHouseImageView.width=" + mHouseImageView.width
                        + " mHouseImageView.height=" + mHouseImageView.height)

                mHouseImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                /**注意！！
                 * 必须要等houseimage 宽高量出来以后才能计算出点的位置
                 */
                if (ipsHouse !== null && ipsHouse!!.mData != null) {
                    var locator: IpsLocator
                    for (locator in ipsHouse!!.mData!!) {
                        if (locator.mIsAdded || locator.mIsSelected) {
                            addDotToHouse(locator)
                        }
                    }
                }
            }
        })

    }

    private fun addDotToHouse(locator: IpsLocator) {

        var dotView = LayoutInflater.from(this).inflate(R.layout.cell_locator_dot, mHouseLayout, false)
        var locatorImageView = dotView!!.findViewById<ImageView>(R.id.image_locator)
        var locatorTextView = dotView!!.findViewById<TextView>(R.id.text_locator)

        locatorImageView.visibility = View.INVISIBLE
        locatorTextView.text = locator.mName

        dotView.setTag(locator)
        dotView.measure(0, 0)
        dotView.x = mInitialWidth * locator.mLocationActual.x + mHouseLayout.width / 2 - mInitialWidth / 2 - dotView.measuredWidth / 2.0f
        dotView.y = mInitialHeight * locator.mLocationActual.y + mHouseLayout.height / 2 - mInitialHeight / 2 - dotView.measuredHeight / 2.0f
        mHouseLayout.addView(dotView, mHouseLayout.childCount)
        android.util.Log.d("ryq", " locator.mLocationActual.x=" + locator.mLocationActual.x
                + " locator.mLocationActual.y=" + locator.mLocationActual.y)
    }
}