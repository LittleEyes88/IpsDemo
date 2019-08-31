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
import com.mercku.ipsdemo.constants.TypeConstants
import com.mercku.ipsdemo.listener.DotTouchListener
import com.mercku.ipsdemo.listener.OnViewTouchListener
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.model.IpsLocator
import com.mercku.ipsdemo.view.BaseEditView
import java.io.File


/**
 * Created by yanqiong.ran on 2019-08-31.
 */
class SurfaceViewActivity : BaseContentActivity() {

    private lateinit var mSurfaceView: SurfaceView
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
        mSurfaceView.setOnTouchListener(mSurfaceTouchListener)

        mIpsHouse = intent.getParcelableExtra<IpsHouse>(ExtraConstants.EXTRA_HOUSE_DETAIL)
        if (mIpsHouse == null)
            return
        mHouseLayout = findViewById(R.id.house_layout)
        mHouseImageView = findViewById<ImageView>(R.id.image_house)
        initHouseLayout(mIpsHouse)
        android.util.Log.d("ryq", "SetHouseLayoutScaleActivity  mIpsHouse.mImageFilePath=" + mIpsHouse.mImageFilePath)
        //mCustomView.setImageBitmap(mIpsHouse.mBitmap)
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
        var intent = Intent(this, HouseLayoutDetailActivity::class.java)
        intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, mIpsHouse)
        startActivity(intent)
    }

    private fun getActualBitmapWidth(): Float {
        return mInitialWidth / (BaseEditView.DEFAULT_PIX_INTERVAL * mSurfaceTouchListener!!.getTotalScaled()) * BaseEditView.DEFAULT_EVERY_GRID_WIDTH

    }

    private fun initHouseLayout(ipsHouse: IpsHouse) {
        android.util.Log.d("ryq", "AddLocatorActivity  ipsHouse.mImageFilePath=" + ipsHouse.mImageFilePath)
        var file = File(ipsHouse.mImageFilePath)
        if (file.exists()) {
            var uri = Uri.fromFile(file)
            mBitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri))
            android.util.Log.d("ryq", " mBitmap!!.width=" + mBitmap!!.width + "  mBitmap!!.height=" + mBitmap!!.height)
            mHouseImageView.setImageBitmap(mBitmap)
        }

        mHouseImageView.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                mBitmap?.let {
                    if (mBitmap!!.width >= mBitmap!!.height) {

                        mInitialWidth = mHouseImageView.width.toFloat()
                        mInitialHeight = mInitialWidth / mBitmap!!.width * mBitmap!!.height
                    } else {
                        mInitialHeight = mHouseImageView.height.toFloat()
                        mInitialWidth = mInitialHeight / mBitmap!!.height * mBitmap!!.width
                    }
                }
                var imageLayoutHeight = mHouseImageView.height
                var imageLayoutWidth = mHouseImageView.width
                android.util.Log.d("ryq", " imageLayoutWidth=" + imageLayoutWidth + " imageLayoutHeight=" + imageLayoutHeight)

                mHouseImageView.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this)
            }
        })
        if (ipsHouse !== null && ipsHouse!!.mData != null) {
            var locator: IpsLocator
            for (locator in ipsHouse!!.mData!!) {
                if (locator.mIsAdded || locator.mIsSelected) {
                    addDotToHouse(locator)
                }
            }
        }
    }

    private fun addDotToHouse(locator: IpsLocator) {

        var dotView = LayoutInflater.from(this).inflate(R.layout.cell_locator_dot, mHouseLayout, false)
        var locatorImageView = dotView!!.findViewById<ImageView>(R.id.image_locator)
        var locatorTextView = dotView!!.findViewById<TextView>(R.id.text_locator)

        locatorImageView.visibility = View.INVISIBLE
        locatorTextView.text = locator.mName

        dotView.setTag(locator)
        dotView.x = mInitialWidth * locator.mLocationActual.x + mHouseImageView.width / 2 - mInitialWidth / 2
        dotView.y = mInitialHeight * locator.mLocationActual.y + mHouseImageView.height / 2 - mInitialHeight / 2
        mHouseLayout.addView(dotView, mHouseLayout.childCount)
        android.util.Log.d("ryq", " locator.mLocationActual.x=" + locator.mLocationActual.x
                + " locator.mLocationActual.y=" + locator.mLocationActual.y)
    }
}