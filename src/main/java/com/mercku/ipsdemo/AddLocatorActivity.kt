package com.mercku.ipsdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.mercku.base.ui.BaseContentActivity
import java.io.File

/**
 * Created by yanqiong.ran on 2019-08-28.
 */
class AddLocatorActivity : BaseContentActivity() {
    private var mBitmap: Bitmap? = null
    private lateinit var mHouseLayoutImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_locator)
        mHouseLayoutImageView = findViewById<ImageView>(R.id.image_house_layout)
        var filePath = intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)
        var file = File(filePath)
        if (file.exists()) {
            var uri = Uri.fromFile(file)
            mBitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri))
            android.util.Log.d("ryq", "AddLocatorActivity  bitmap=" + mBitmap)
            mHouseLayoutImageView.setImageBitmap(mBitmap)
        }
        mHouseLayoutImageView.setOnTouchListener(ImageTouchListener(mHouseLayoutImageView))
        mHouseLayoutImageView.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                var houseLayoutHeight = mHouseLayoutImageView.height
                var houseLayoutWidth = mHouseLayoutImageView.width
                android.util.Log.d("ryq", " houseLayoutHeight=" + houseLayoutHeight)
                android.util.Log.d("ryq", " houseLayoutWidth=" + houseLayoutWidth)
                var matrix = mHouseLayoutImageView.imageMatrix
                if (mBitmap != null) {
                    android.util.Log.d("ryq", " mBitmap!!.width=" + mBitmap!!.width + "  mBitmap!!.height=" + mBitmap!!.height)
                    matrix.postTranslate((houseLayoutWidth / 2).toFloat() - mBitmap!!.width / 2, (houseLayoutHeight / 2).toFloat() - mBitmap!!.height / 2)
                } else {
                    matrix.postTranslate((houseLayoutWidth / 2).toFloat(), (houseLayoutHeight / 2).toFloat())
                }

                mHouseLayoutImageView.imageMatrix = matrix

                mHouseLayoutImageView.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this)
            }
        })


    }

}
