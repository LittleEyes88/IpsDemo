package com.mercku.ipsdemo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.*
import com.mercku.ipsdemo.adapter.LocatorAdapter
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.constants.TypeConstants
import com.mercku.ipsdemo.listener.DotTouchListener
import com.mercku.ipsdemo.listener.ImageTouchListener
import com.mercku.ipsdemo.listener.OnDotMoveFinishListener
import com.mercku.ipsdemo.listener.OnItemClickListener
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.model.IpsLocator
import com.mercku.ipsdemo.view.BaseEditView
import java.io.File

/**
 * Created by yanqiong.ran on 2019-08-28.
 */
class AddLocatorActivity : BaseContentActivity(), OnItemClickListener, OnDotMoveFinishListener {

    private lateinit var mImageTouchListener: ImageTouchListener
    private var mBitmapTransX: Float = 0f
    private var mBitmapTransY: Float = 0f
    private lateinit var mData: ArrayList<IpsLocator>
    private lateinit var mRecyclerView: RecyclerView
    private var mBitmap: Bitmap? = null
    private lateinit var mHouseImageView: ImageView

    private lateinit var mHouseLayout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_locator)
        setRightTitleText(getString(R.string.text_next))
        mHouseLayout = findViewById(R.id.house_layout)
        mHouseImageView = findViewById<ImageView>(R.id.image_house)
        initHouseLayout()

        mRecyclerView = findViewById(R.id.recycler_view)
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayout.HORIZONTAL
        mRecyclerView.layoutManager = layoutManager

        mData = ArrayList<IpsLocator>(4)
        mData.add(IpsLocator("M3", "M3", "id00"))
        mData.add(IpsLocator("Bee1", "Bee", "id01"))
        mData.add(IpsLocator("Bee2", "Bee", "id02"))
        mData.add(IpsLocator("Bee3", "Bee", "id03"))
        mRecyclerView.adapter = LocatorAdapter(this, mData, this)

    }

    private fun initHouseLayout() {
        var filePath = intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)
        android.util.Log.d("ryq", "AddLocatorActivity  filePath=" + filePath)
        var file = File(filePath)
        if (file.exists()) {
            var uri = Uri.fromFile(file)
            mBitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri))

            mHouseImageView.setImageBitmap(mBitmap)
        }
        mImageTouchListener = ImageTouchListener(mHouseImageView)
        mHouseImageView.setOnTouchListener(mImageTouchListener)
        mHouseImageView.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                var houseLayoutHeight = mHouseImageView.height
                var houseLayoutWidth = mHouseImageView.width
                android.util.Log.d("ryq", " houseLayoutHeight=" + houseLayoutHeight)
                android.util.Log.d("ryq", " houseLayoutWidth=" + houseLayoutWidth)
                var matrix = mHouseImageView.imageMatrix
                if (mBitmap != null) {
                    mBitmapTransX = (houseLayoutWidth / 2).toFloat() - mBitmap!!.width / 2
                    mBitmapTransY = (houseLayoutHeight / 2).toFloat() - mBitmap!!.height / 2
                    android.util.Log.d("ryq", " mBitmap!!.width=" + mBitmap!!.width + "  mBitmap!!.height=" + mBitmap!!.height)
                } else {
                    mBitmapTransX = (houseLayoutWidth / 2).toFloat()
                    mBitmapTransY = (houseLayoutHeight / 2).toFloat()
                }
                matrix.postTranslate(mBitmapTransX, mBitmapTransY)
                mHouseImageView.imageMatrix = matrix

                mHouseImageView.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this)
            }
        })

    }


    override fun onItemClick(position: Int, viewId: Int) {
        if (mBitmap == null) {
            Toast.makeText(this, getString(R.string.choose_one_picture), Toast.LENGTH_LONG).show()
        }
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
        //如果已经被添加，则直接显示
        var alreadyAdd = false
        if ((ipsLocator.mIsAdded || ipsLocator.mIsSelected)
                && (position + 1) < mHouseLayout.childCount) {
            var viewIndex = 0
            while (viewIndex < mHouseLayout.childCount) {
                //找到对应的view
                var view = mHouseLayout.getChildAt(viewIndex)
                if (view.tag != null && view.tag is IpsLocator) {
                    var locator = view.tag as IpsLocator
                    if (locator.mId.equals(ipsLocator.mId)) {
                        var locatorImageView = view.findViewById<ImageView>(R.id.image_locator)
                        if (locatorImageView != null) {
                            locatorImageView.visibility = View.VISIBLE
                        }
                        alreadyAdd = true
                        break;
                    }
                }
                viewIndex++
            }

            //如果未被添加，则添加
        }
        if (!alreadyAdd) {
            addDotToHouse(ipsLocator)
        }

    }

    private fun addDotToHouse(locator: IpsLocator) {

        var dotView = LayoutInflater.from(this).inflate(R.layout.cell_locator_dot, mHouseLayout, false)


        var locatorImageView = dotView!!.findViewById<ImageView>(R.id.image_locator)
        var locatorTextView = dotView!!.findViewById<TextView>(R.id.text_locator)

        when (locator.mType) {
            TypeConstants.TYPE_M3 -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_m3)

            }
            TypeConstants.TYPE_Bee -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_bee)

            }
        }
        locatorTextView.text = locator.mName
        locator.mLocation.y = (mHouseLayout.height / 2).toFloat()
        locator.mLocation.x = (mHouseLayout.width / 2).toFloat()
        dotView!!.setOnTouchListener(DotTouchListener(dotView!!, locator.mId, this))
        android.util.Log.d("ryq", "addDotToHouse mHouseLayout.childCount=" + mHouseLayout.childCount)
        dotView.setTag(locator)
        mHouseLayout.addView(dotView, mHouseLayout.childCount)
    }

    override fun onFinish(x: Float, y: Float, id: String, targetView: View) {
        if (mBitmap != null) {

            android.util.Log.d("ryq", "onFinish x=" + x + " y=" + y
                    + " mHouseImageView!!.left=" + mHouseImageView!!.left
                    + " mHouseImageView!!.right=" + mHouseImageView!!.right
                    + " mHouseImageView!!.top=" + mHouseImageView!!.top
                    + " mHouseImageView!!.bottom=" + mHouseImageView!!.bottom)
            for (ipsLocator in mData) {
                if (ipsLocator.mId.equals(id)) {
                    if (ipsLocator.mLocation.x < mHouseImageView!!.left
                            || ipsLocator.mLocation.x > mHouseImageView!!.right
                            || ipsLocator.mLocation.y < mHouseImageView!!.top
                            || ipsLocator.mLocation.y > mHouseImageView!!.bottom) {
                        Toast.makeText(this, getString(R.string.move_locator_in_house), Toast.LENGTH_LONG).show()
                    } else {

                        var locatorTextView = targetView.findViewById<TextView>(R.id.text_locator)
                        locatorTextView.visibility = View.VISIBLE
                        var locatorImageView = targetView.findViewById<ImageView>(R.id.image_locator)
                        locatorImageView.visibility = View.INVISIBLE


                        Log.d(BaseEditView.TAG, "onFinish x=" + x + " y=" + y
                                + " targetView.width =" + targetView.width
                                + " targetView.height =" + targetView.height)
                        ipsLocator.mLocation.x = x
                        ipsLocator.mLocation.y = y
                    }

                }
            }
        }

    }


    override fun onClickRightTitleView() {
        intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)?.let {
            var filePath = intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)
            android.util.Log.d("ryq", "onClickRightTitleView  filePath=" + filePath)
            calculateEveryDotLocation()
            var house = IpsHouse(mData, resources.getString(R.string.my_home), System.currentTimeMillis().toString(), filePath)
            var intent = Intent(this, SetHouseLayoutScaleActivity::class.java)
            intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, house)
            startActivity(intent)
        }


    }

    private fun calculateEveryDotLocation() {


        var scaled = mImageTouchListener.getTotalScaled()

        Log.d(BaseEditView.TAG, "onFinish scaled =" + scaled)
        var index = 0
        while (index < mData.size) {
            var ipsLocator = mData[index]
            if (ipsLocator.mLocation.x > 0 && ipsLocator.mLocation.y > 0) {
                var curDotLayoutView = mHouseLayout.getChildAt(index + 1)
                var viewIndex = 0

                while (viewIndex < mHouseLayout.childCount) {
                    //找到对应的view
                    var view = mHouseLayout.getChildAt(viewIndex)
                    if (view.tag != null && view.tag is IpsLocator) {
                        var locator = view.tag as IpsLocator
                        if (locator.mId.equals(ipsLocator.mId)) {
                            var radius = 0
                            var locatorDotImageView = curDotLayoutView!!.findViewById<ImageView>(R.id.image_locator_dot)
                            if (locatorDotImageView != null) {
                                radius = locatorDotImageView.height / 2
                            }

                            //左上角的坐标
                            ipsLocator.mLocationActual.x = (ipsLocator.mLocation.x / scaled + curDotLayoutView!!.width / 2 - mBitmapTransX - radius) / mBitmap!!.width
                            ipsLocator.mLocationActual.y = (ipsLocator.mLocation.y / scaled + curDotLayoutView!!.height / 2 - mBitmapTransY - radius) / mBitmap!!.height
                            break;
                        }
                    }
                    viewIndex++
                }


            }
            index++
        }


    }
}
