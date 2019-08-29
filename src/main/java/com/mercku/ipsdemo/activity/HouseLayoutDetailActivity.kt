package com.mercku.ipsdemo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.listener.ImageTouchListener
import com.mercku.ipsdemo.model.IpsHouse
import java.io.File

class HouseLayoutDetailActivity : BaseContentActivity() {
    private lateinit var mCustomView: com.mercku.ipsdemo.view.MyLocatorView
    private var mBitmap: Bitmap? = null
    private var mIpsHouse: IpsHouse? = null
    private lateinit var mHouseImageView: ImageView
    private lateinit var mHouseLayout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_layout_detail)
        setRightTitleText(getString(R.string.family_list))
        //mHouseLayout = findViewById(R.id.house_layout)
        //mHouseImageView = findViewById<ImageView>(R.id.image_house)
        mCustomView = findViewById(R.id.layout_custom_view)


        mIpsHouse = intent.getParcelableExtra<IpsHouse>(ExtraConstants.EXTRA_HOUSE_DETAIL)
        if (mIpsHouse == null) {
            return
        }
        mCustomView.setHouseDetail(mIpsHouse)
        //initHouseLayout()
    }

    override fun onClickRightTitleView() {
        var intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    /*private fun initHouseLayout() {

        var file = File(mIpsHouse!!.mImageFilePath)
        if (file.exists()) {
            var uri = Uri.fromFile(file)
            mBitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri))

            mHouseImageView.setImageBitmap(mBitmap)
        }
        mHouseImageView.setOnTouchListener(ImageTouchListener(mHouseImageView))
        mHouseImageView.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                var houseLayoutHeight = mHouseImageView.height
                var houseLayoutWidth = mHouseImageView.width
                android.util.Log.d("ryq", " houseLayoutHeight=" + houseLayoutHeight)
                android.util.Log.d("ryq", " houseLayoutWidth=" + houseLayoutWidth)
                var matrix = mHouseImageView.imageMatrix
                if (mBitmap != null) {
                    android.util.Log.d("ryq", " mBitmap!!.width=" + mBitmap!!.width + "  mBitmap!!.height=" + mBitmap!!.height)
                    matrix.postTranslate((houseLayoutWidth / 2).toFloat() - mBitmap!!.width / 2, (houseLayoutHeight / 2).toFloat() - mBitmap!!.height / 2)
                } else {
                    matrix.postTranslate((houseLayoutWidth / 2).toFloat(), (houseLayoutHeight / 2).toFloat())
                }

                mHouseImageView.imageMatrix = matrix

                mHouseImageView.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this)
            }
        })

    }*/

    public fun onClickSearch(view: View) {
        var intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, mIpsHouse)
        startActivity(intent)
    }
}
