package com.mercku.ipsdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
    private lateinit var mHouseImageView: ImageView

    private lateinit var mHouseLayout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_locator)

        mHouseLayout = findViewById(R.id.house_layout)
        mHouseImageView = findViewById<ImageView>(R.id.image_house)
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

    }

    override fun onClick(view: View?) {
        when (view?.id) {

        }
    }

    override fun onItemClick(position: Int, viewId: Int) {
        var ipsLocator = mData[position]
        ipsLocator.mIsSelected = true
        var pos = 0
        android.util.Log.d("ryq", "onItemClick position=" + position)
        while (pos < mData.size) {
            var locator = mData[pos]
            android.util.Log.d("ryq", "onItemClick pos=" + pos + " locator.mIsSelected=" + locator.mIsSelected)
            if (pos != position && locator.mIsSelected) {
                locator.mIsAdded = true
            }
            pos++
        }
        mRecyclerView.adapter?.notifyDataSetChanged()
        addDotToHouse(ipsLocator, position)
    }

    private fun addDotToHouse(locator: IpsLocator, position: Int) {
        var dotView = LayoutInflater.from(this).inflate(R.layout.cell_locator_dot, mHouseLayout, false)
        var locatorImageView = dotView.findViewById<ImageView>(R.id.image_locator)
        var locatorTextView = dotView.findViewById<TextView>(R.id.text_locator)

        when (locator.mType) {
            TypeConstants.TYPE_M3 -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_m3)

            }
            TypeConstants.TYPE_Bee -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_bee)

            }
        }
        locatorTextView.text = locator.mName

        dotView.setOnTouchListener(DotMoveListener(dotView))
        android.util.Log.d("ryq", "addDotToHouse mHouseLayout.childCount=" + mHouseLayout.childCount)
        mHouseLayout.addView(dotView, mHouseLayout.childCount)
    }


}
