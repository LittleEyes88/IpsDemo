package com.mercku.ipsdemo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.adapter.LocatorAdapter
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.constants.TypeConstants
import com.mercku.ipsdemo.listener.*
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.model.IpsLocator
import com.mercku.ipsdemo.util.BitmapUtil
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by yanqiong.ran on 2019-08-28.
 */
class AddLocatorActivity : BaseContentActivity(), OnItemClickListener, OnDotMoveFinishListener, OnStopAnimListener {

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
    private lateinit var mHandImageView: ImageView

    private lateinit var mHouseLayout: ViewGroup

    private var mSelectPos = RecyclerView.NO_POSITION
    /**
     * data中的顺序与子元素添加顺序的映射
     */
    private val mPositionIndexMap = SparseIntArray()
    private var mNeedAnim = true
    private lateinit var mTranslateAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_locator)
        setRightTitleText(getString(R.string.text_next))
        setRightTitleEnable(R.color.text_color_right, false)
        mTopHintTextView = findViewById<TextView>(R.id.text_top_hint)
        mBottomHintTextView = findViewById<TextView>(R.id.text_bottom_hint)
        mHouseLayout = findViewById(R.id.house_layout)
        mHouseImageView = findViewById<ImageView>(R.id.image_house)
        initHouseLayout()

        mRecyclerView = findViewById(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        mRecyclerView.layoutManager = layoutManager

        mData = ArrayList<IpsLocator>(4)
        mData.add(IpsLocator("M3", "M3", "id00"))
        mData.add(IpsLocator("Bee1", "Bee", "id01"))
        mData.add(IpsLocator("Bee2", "Bee", "id02"))
        mData.add(IpsLocator("Bee3", "Bee", "id03"))
        mRecyclerView.adapter = LocatorAdapter(this, mData, this)

        mHandImageView = ImageView(this)
        mHandImageView.setImageResource(R.drawable.ic_hand)
        mHouseLayout.post {
            val x = mHouseLayout.width / 2.0f + mHouseImageView.translationX
            val y = mHouseLayout.height / 2.0f + mHouseImageView.translationY
            mTranslateAnim = TranslateAnimation(x - 100, x + 100, y - 100, y + 100)
            mTranslateAnim.duration = 500
            mTranslateAnim.repeatCount = TranslateAnimation.INFINITE
            mTranslateAnim.repeatMode = TranslateAnimation.REVERSE
        }
    }

    private fun initHouseLayout() {
        val filePath = intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)
        Log.d("ryq", "AddLocatorActivity  filePath=$filePath")
        val file = File(filePath)
        if (file.exists()) {
            val uri = Uri.fromFile(file)
            mBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            Log.d("ryq", " mBitmap!!.width=" + mBitmap!!.width + "  mBitmap!!.height=" + mBitmap!!.height)
            mHouseImageView.setImageBitmap(mBitmap)
        }

        mImageTouchListener = ImageTouchListener(mHouseLayout, this)
        mHouseImageView.setOnTouchListener(mImageTouchListener)
        mHouseImageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBitmap?.let {
                    val scale = BitmapUtil.getScaleAfterResizeBitmap(it, mHouseImageView.width, mHouseImageView.height)
                    mInitialHeight = it.height.toFloat() * scale
                    mInitialWidth = it.width * scale
                }

                Log.d("ryq", " mInitialWidth=" + mInitialWidth
                        + " mInitialHeight=" + mInitialHeight
                        + " mHouseImageView.width=" + mHouseImageView.width
                        + " mHouseImageView.height=" + mHouseImageView.height)

                mHouseImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        mHouseLayout.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val houseLayoutHeight = mHouseLayout.height
                        val houseLayoutWidth = mHouseLayout.width
                        Log.d("ryq", " houseLayoutHeight=$houseLayoutHeight houseLayoutWidth=$houseLayoutWidth")

                        mHouseLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
    }

    override fun onItemClick(position: Int, viewId: Int) {
        mBottomHintTextView.visibility = if (mHouseLayout.childCount <= 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
        setRightTitleEnable(R.color.text_color_right, true)
        mTopHintTextView.visibility = View.VISIBLE

        //刷新当前选择项
        mData[position].mIsSelected = !mData[position].mIsSelected
        mData[position].mIsAdded = true
        val currentView = mHouseLayout.getChildAt(mPositionIndexMap.get(position, -1))
        if (currentView == null) {
            addDotToHouse(position)
            //因为houselayout已经有了一个imageview子元素
            if (mPositionIndexMap.size() == 0) {
                mPositionIndexMap.put(position, 1)
            } else {
                mPositionIndexMap.put(position, mHouseLayout.childCount - 1)
            }
        } else {
            refreshLocatorVisible(currentView, mData[position].mIsSelected)
        }
        if (mHouseLayout.childCount == 2 && mNeedAnim) {
            mHouseLayout.addView(mHandImageView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mHandImageView.startAnimation(mTranslateAnim)
            mNeedAnim = false
        } else if (mHouseLayout.childCount >= 1 && mHouseLayout.indexOfChild(mHandImageView) != -1) {
            mHandImageView.clearAnimation()
            mHouseLayout.removeView(mHandImageView)
        }
        //如果这次跟上次的选择相同，设置为无效值
        if (position == mSelectPos) {
            mSelectPos = RecyclerView.NO_POSITION
            mRecyclerView.adapter?.notifyItemChanged(position)
            return
        }
        //刷新上次选择项
        if (mSelectPos != RecyclerView.NO_POSITION && mSelectPos != position) {
            mData[mSelectPos].mIsSelected = !mData[mSelectPos].mIsSelected
            val oldView = mHouseLayout.getChildAt(mPositionIndexMap.get(mSelectPos, -1))
            refreshLocatorVisible(oldView, mData[mSelectPos].mIsSelected)
            mRecyclerView.adapter?.notifyItemChanged(mSelectPos)
        }
        mRecyclerView.adapter?.notifyItemChanged(position)
        mSelectPos = position
    }

    private fun refreshLocatorVisible(view: View?, isSelected: Boolean) {
        val locatorImageView = view?.findViewById<ImageView>(R.id.image_locator)
        locatorImageView?.visibility = if (isSelected) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun addDotToHouse(index: Int) {
        val dotView = LayoutInflater.from(this).inflate(R.layout.cell_locator_dot, mHouseLayout, false)
        val locatorImageView = dotView!!.findViewById<ImageView>(R.id.image_locator)
        val locatorTextView = dotView.findViewById<TextView>(R.id.text_locator)
        dotView.setOnClickListener(DotClickListener(this, index))
        val locator = mData[index]
        when (locator.mType) {
            TypeConstants.TYPE_M3 -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_m3)
            }
            TypeConstants.TYPE_Bee -> {
                locatorImageView.setImageResource(R.drawable.selector_coordinate_bee)
            }
        }
        locatorTextView.text = locator.mName

        dotView.setOnTouchListener(DotTouchListener(dotView, locator, this, this))
        Log.d("ryq", "addDotToHouse mHouseLayout.childCount=" + mHouseLayout.childCount
                + " mHouseImageView.width=" + mHouseImageView.width + " mHouseImageView.height=" + mHouseImageView.height)
        Log.d("ryq", " mHouseImageView.measuredWidth=" + mHouseImageView.measuredWidth
                + " mHouseImageView.measuredHeight=" + mHouseImageView.measuredHeight
                + " mHouseImageView.translationX=" + mHouseImageView.translationX
                + " mHouseImageView.translationY=" + mHouseImageView.translationY)

        dotView.measure(0, 0)
        dotView.x = mHouseLayout.width / 2.0f - dotView.measuredWidth / 2.0f + mHouseImageView.translationX
        dotView.y = mHouseLayout.height / 2.0f - dotView.measuredHeight / 2.0f + mHouseImageView.translationY

        dotView.tag = locator

        mHouseLayout.addView(dotView)
        Log.d("ryq", " dotView.x=" + dotView.x + " dotView.y=" + dotView.y)
        Log.d("ryq", "mHouseLayout.pivotX=" + mHouseLayout.pivotX + " mHouseLayout.pivotY=" + mHouseLayout.pivotY)
    }

    override fun onFinish(dx: Float, dy: Float, id: String, targetView: View) {
        if (mBitmap != null) {
            for (ipsLocator in mData) {
                if (ipsLocator.mId == id) {
                    ipsLocator.mLocationActual.x += dx / (mInitialWidth * mImageTouchListener.getTotalScaled())
                    ipsLocator.mLocationActual.y += dy / (mInitialHeight * mImageTouchListener.getTotalScaled())
                    ipsLocator.mIsAdded = true
                    break
                }
            }
        }

    }


    override fun onClickRightTitleView() {
        intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)?.let {
            val filePath = intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)
            Log.d("ryq", "onClickRightTitleView  filePath=$filePath")
            // calculateEveryDotLocation()
            val house = IpsHouse(mData, resources.getString(R.string.my_home), System.currentTimeMillis().toString(), filePath)
            val intent = Intent(this, SurfaceViewActivity::class.java)
            intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, house)
            startActivity(intent)
        }
    }

    override fun stopAnim() {
        mHandImageView.clearAnimation()
        mHouseLayout.removeViewInLayout(mHandImageView)
    }


    class DotClickListener(activity: AddLocatorActivity, private val position: Int) : View.OnClickListener {
        private val mWeakReference = WeakReference(activity)
        override fun onClick(v: View?) {

            mWeakReference.get()?.let {
                for (i in 0 until it.mData.size) {
                    it.mData[i].mIsSelected = i == position
                }
                it.mSelectPos = position
                it.mRecyclerView.adapter?.notifyDataSetChanged()
                val index = it.mPositionIndexMap[position]
                for (i in 1 until it.mHouseLayout.childCount) {
                    val imageView = it.mHouseLayout.getChildAt(i).findViewById<ImageView>(R.id.image_locator)
                    imageView.visibility = if (index == i) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                }
            }

        }
    }

}
