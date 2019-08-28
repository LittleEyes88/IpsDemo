package com.mercku.ipsdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercku.base.ui.BaseContentActivity
import java.io.File

/**
 * Created by yanqiong.ran on 2019-08-28.
 */
class AddLocatorActivity : BaseContentActivity(), View.OnClickListener, OnItemClickListener {

    private lateinit var mData: ArrayList<IpsLocator>
    private lateinit var mRecyclerView: RecyclerView
    private var mBitmap: Bitmap? = null
    private lateinit var mHouseLayoutImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_locator)

        mHouseLayoutImageView = findViewById<ImageView>(R.id.image_house_layout)
        initHouseLayout()

        mRecyclerView = findViewById(R.id.recycler_view)
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayout.HORIZONTAL
        mRecyclerView.layoutManager = layoutManager

        mData = ArrayList<IpsLocator>(4)
        mData.add(IpsLocator("M3", "M3"))
        mData.add(IpsLocator("Bee1", "Bee"))
        mData.add(IpsLocator("Bee2", "Bee"))
        mData.add(IpsLocator("Bee3", "Bee"))
        mRecyclerView.adapter = LocatorAdapter(this, mData, this)

    }

    private fun initHouseLayout() {
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

    override fun onClick(view: View?) {
        when (view?.id) {

        }
    }

    override fun onItemClick(position: Int, viewId: Int) {
        var ipsLocator = mData[position]
        ipsLocator.mIsSelected = true
        var pos = 0
        while (pos < mData.size) {
            var locator = mData[position]
            if (pos != position && locator.mIsSelected) {
                locator.mIsAdded = true
            }
        }
        mRecyclerView.adapter?.notifyDataSetChanged()
        //todo ,add icons
    }


}
