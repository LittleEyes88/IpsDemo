package com.mercku.ipsdemo.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.model.IpsHouse
import java.io.File

/**
 * Created by yanqiong.ran on 2019-08-29.
 */
class SetHouseLayoutScaleActivity : BaseContentActivity() {

    private lateinit var mIpsHouse: IpsHouse
    private lateinit var mHintImageView: ImageView
    private lateinit var mHintTextView: TextView
    private lateinit var mCustomView: com.mercku.ipsdemo.view.MyHouseView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_house_layout_scale)
        setRightTitleText(getString(R.string.save))
        mCustomView = findViewById(R.id.layout_custom_view)
        mIpsHouse = intent.getParcelableExtra<IpsHouse>(ExtraConstants.EXTRA_HOUSE_DETAIL)
        if (mIpsHouse == null)
            return
       /* var file = File(mIpsHouse.mImageFilePath)
        if (file.exists()) {
            var uri = Uri.fromFile(file)
            var bitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri))
            android.util.Log.d("ryq", "SetHouseLayoutScaleActivity  bitmap=" + bitmap)
            mCustomView.setImageBitmap(bitmap)
        }*/
        android.util.Log.d("ryq", "SetHouseLayoutScaleActivity  mIpsHouse.mImageFilePath=" + mIpsHouse.mImageFilePath)
        //mCustomView.setImageBitmap(mIpsHouse.mBitmap)
        mCustomView.setHouseDetail(mIpsHouse)
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
        //todo save the layout
    }
}