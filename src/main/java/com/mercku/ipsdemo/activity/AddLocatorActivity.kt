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
import com.mercku.ipsdemo.util.BitmapUtil
import com.mercku.ipsdemo.view.BaseEditView
import java.io.File

/**
 * Created by yanqiong.ran on 2019-08-28.
 */
class AddLocatorActivity : BaseContentActivity(), OnItemClickListener, OnDotMoveFinishListener {

    private lateinit var mTopHintTextView: TextView
    private lateinit var mBottomHintTextView: TextView
    private var mInitialWidth: Float = 0f

    private var mInitialHeight: Float = 0f
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
        mTopHintTextView = findViewById<TextView>(R.id.text_top_hint)
        mBottomHintTextView = findViewById<TextView>(R.id.text_bottom_hint)
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
            android.util.Log.d("ryq", " mBitmap!!.width=" + mBitmap!!.width + "  mBitmap!!.height=" + mBitmap!!.height)
            mHouseImageView.setImageBitmap(mBitmap)
        }

        mImageTouchListener = ImageTouchListener(mHouseLayout)
        mHouseImageView.setOnTouchListener(mImageTouchListener)
        mHouseImageView.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                mBitmap?.let {
                    var scale = BitmapUtil.getScaleAfterResizeBitmap(mBitmap!!, mHouseImageView.width, mHouseImageView.height)
                    mInitialHeight = mBitmap!!.height.toFloat() * scale
                    mInitialWidth = mBitmap!!.width * scale
                }

                android.util.Log.d("ryq", " mInitialWidth=" + mInitialWidth
                        + " mInitialHeight=" + mInitialHeight
                        + " mHouseImageView.width=" + mHouseImageView.width
                        + " mHouseImageView.height=" + mHouseImageView.height)

                mHouseImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
            }
        })
        mHouseLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {

                        var houseLayoutHeight = mHouseLayout.height
                        var houseLayoutWidth = mHouseLayout.width
                        android.util.Log.d("ryq", " houseLayoutHeight=" + houseLayoutHeight + " houseLayoutWidth=" + houseLayoutWidth)

                        mHouseLayout.getViewTreeObserver()
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
        while (pos < mData.size) {
            var locator = mData[pos]
            android.util.Log.d("ryq", "onItemClick pos=" + pos + " locator.mIsSelected=" + locator.mIsSelected)
            if (pos != position) {
                if (locator.mIsSelected) {
                    locator.mIsAdded = true
                }
                locator.mIsSelected = false
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

        mBottomHintTextView.visibility = if (mHouseLayout.childCount <= 1) {
            View.VISIBLE
        } else {
            View.GONE
        }

        mTopHintTextView.visibility = View.VISIBLE
        updateHouseChildView()
    }

    fun updateHouseChildView() {
        var index = 0
        while (index < mHouseLayout.childCount) {
            var view = mHouseLayout.getChildAt(index)
            if (view != null && view.tag != null && view.tag is IpsHouse) {
                var ipsHouse = view.tag as IpsHouse
                var locatorImageView = view!!.findViewById<ImageView>(R.id.image_locator)
                locatorImageView.visibility = if (ipsHouse.isSelected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            index++
        }
    }

    private fun addDotToHouse(locator: IpsLocator) {
        var dotView = LayoutInflater.from(this).inflate(R.layout.cell_locator_dot, mHouseLayout, false)
        var locatorImageView = dotView!!.findViewById<ImageView>(R.id.image_locator)
        var locatorTextView = dotView!!.findViewById<TextView>(R.id.text_locator)
        dotView.setOnClickListener {
            locatorImageView.visibility = View.VISIBLE
            locator.mIsSelected = true
        }
        when (locator.mType) {
            TypeConstants.TYPE_M3 -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_m3)

            }
            TypeConstants.TYPE_Bee -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_bee)

            }
        }
        locatorTextView.text = locator.mName

        dotView!!.setOnTouchListener(DotTouchListener(dotView!!, locator, this))
        android.util.Log.d("ryq", "addDotToHouse mHouseLayout.childCount=" + mHouseLayout.childCount
                + " mHouseImageView.width=" + mHouseImageView.width + " mHouseImageView.height=" + mHouseImageView.height)
        android.util.Log.d("ryq", " mHouseImageView.measuredWidth=" + mHouseImageView.measuredWidth
                + " mHouseImageView.measuredHeight=" + mHouseImageView.measuredHeight
                + " mHouseImageView.translationX=" + mHouseImageView.translationX
                + " mHouseImageView.translationY=" + mHouseImageView.translationY)

        dotView.measure(0, 0)
        dotView.x = mHouseLayout.width / 2.0f - dotView.measuredWidth / 2.0f + mHouseImageView.translationX
        dotView.y = mHouseLayout.height / 2.0f - dotView.measuredHeight / 2.0f + mHouseImageView.translationY

        dotView.setTag(locator)

        mHouseLayout.addView(dotView, mHouseLayout.childCount)
        android.util.Log.d("ryq", " dotView.x=" + dotView.x + " dotView.y=" + dotView.y)
        android.util.Log.d("ryq", "mHouseLayout.pivotX=" + mHouseLayout.pivotX + " mHouseLayout.pivotY=" + mHouseLayout.pivotY)
    }

    override fun onFinish(dx: Float, dy: Float, id: String, targetView: View) {
        if (mBitmap != null) {

            android.util.Log.d("ryq", "onFinish "
                    + " mHouseImageView!!.left=" + mHouseImageView!!.left
                    + " mHouseImageView!!.right=" + mHouseImageView!!.right
                    + " mHouseImageView!!.top=" + mHouseImageView!!.top
                    + " mHouseImageView!!.bottom=" + mHouseImageView!!.bottom
                    + " targetView.x=" + targetView.x
                    + " targetView.y=" + targetView.y)
            for (ipsLocator in mData) {
                if (ipsLocator.mId.equals(id)) {
                    /* if (targetView.x < mHouseImageView!!.left
                             || targetView.x > mHouseImageView!!.right
                             || targetView.y < mHouseImageView!!.top
                             || targetView.y > mHouseImageView!!.bottom) {
                         Toast.makeText(this, getString(R.string.move_locator_in_house), Toast.LENGTH_LONG).show()
                     } else {*/

                    var locatorTextView = targetView.findViewById<TextView>(R.id.text_locator)
                    locatorTextView.visibility = View.VISIBLE
                    var locatorImageView = targetView.findViewById<ImageView>(R.id.image_locator)
                    locatorImageView.visibility = View.INVISIBLE

                    Log.d(BaseEditView.TAG, "onFinish dx=" + dx + " dy=" + dy
                            + " targetView.width =" + targetView.width
                            + " targetView.height =" + targetView.height)
                    ipsLocator.mLocationActual.x += dx / (mInitialWidth * mImageTouchListener.getTotalScaled())
                    ipsLocator.mLocationActual.y += dy / (mInitialHeight * mImageTouchListener.getTotalScaled())
                    Log.d(BaseEditView.TAG, "onFinish dx=" + dx + " dy=" + dy
                            + "  ipsLocator.mLocationActual.x  =" + ipsLocator.mLocationActual.x
                            + " ipsLocator.mLocationActual.y=" + ipsLocator.mLocationActual.y)
                    // }
                    ipsLocator.mIsAdded = true
                    break
                }
            }
        }

    }


    override fun onClickRightTitleView() {
        intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)?.let {
            var filePath = intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)
            android.util.Log.d("ryq", "onClickRightTitleView  filePath=" + filePath)
            // calculateEveryDotLocation()
            var house = IpsHouse(mData, resources.getString(R.string.my_home), System.currentTimeMillis().toString(), filePath)
            var intent = Intent(this, SurfaceViewActivity::class.java)
            intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, house)
            startActivity(intent)
        }


    }

}
