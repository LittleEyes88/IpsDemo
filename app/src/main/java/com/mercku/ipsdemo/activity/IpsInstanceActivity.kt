package com.mercku.ipsdemo.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.fragment.IpsInstanceStep1Fragment
import com.mercku.ipsdemo.fragment.IpsInstanceStep2Fragment
import com.mercku.ipsdemo.fragment.IpsInstanceStep3Fragment
import com.mercku.ipsdemo.fragment.IpsInstanceStep4Fragment
import com.mercku.ipsdemo.util.ActivityUtils
import java.lang.ref.WeakReference

class IpsInstanceActivity : BaseContentActivity() {
    private lateinit var mViewPager: ViewPager
    private lateinit var mIndicatorLayout: LinearLayout
    private var mCurrentIndicatorPos: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ips_instance)
        mViewPager = findViewById(R.id.view_pager)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                changeIndicator(position)
            }
        })
        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(IpsInstanceStep1Fragment())
        fragmentList.add(IpsInstanceStep2Fragment())
        fragmentList.add(IpsInstanceStep3Fragment())
        fragmentList.add(IpsInstanceStep4Fragment())
        val adapter = ViewPagerAdapter(fragmentList, supportFragmentManager)
        mViewPager.adapter = adapter
        mIndicatorLayout = findViewById(R.id.layout_indicator)
        for (i in 0 until fragmentList.size) {
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.leftMargin = ActivityUtils.dp2px(this, 5)
            layoutParams.rightMargin = ActivityUtils.dp2px(this, 5)
            val resId = if (0 == i) {
                R.drawable.shape_indicator_black
            } else {
                R.drawable.shape_indicator_grey
            }
            imageView.setImageResource(resId)
            mIndicatorLayout.addView(imageView, layoutParams)
            imageView.setOnClickListener(ButtonClickListener(this, i))
        }
        mViewPager.currentItem = 0
    }

    private fun changeIndicator(position: Int) {
        for (i in 0 until mIndicatorLayout.childCount) {
            val imageView = mIndicatorLayout.getChildAt(i) as ImageView
            val resId = if (position == i) {
                R.drawable.shape_indicator_black
            } else {
                R.drawable.shape_indicator_grey
            }
            imageView.setImageResource(resId)
        }
        mCurrentIndicatorPos = position
    }

    class ButtonClickListener(activity: IpsInstanceActivity, private val position: Int) : View.OnClickListener {
        private val mWeakReference = WeakReference(activity)
        override fun onClick(v: View?) {
            mWeakReference.get()?.let {
                it.mViewPager.currentItem = position
            }
        }
    }

    class ViewPagerAdapter(private val fragmentList: List<Fragment>, fragmentManager: FragmentManager) :
            FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }
}