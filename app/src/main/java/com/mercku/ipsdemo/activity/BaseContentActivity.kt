package com.mercku.ipsdemo.activity

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.mercku.ipsdemo.R

/**
 * An activity with a customized action bar. It also inherits
 * [BaseActivity] for the analytics functions.
 */
open class BaseContentActivity : BaseActivity() {

    private var mActionBarLayoutId = R.layout.ab_mercku

    private var mActionBarOverlay = false

    var mTitleView: TextView? = null

    private var mRightTitleView: TextView? = null

    private var mRightImageView: ImageView? = null

    private var mMiddleTitleView: TextView? = null

    private var mRightLayoutView: View? = null

    private var mMiddleLayoutView: View? = null

    private var mTopTitleDividerLineView: View? = null

    protected val contentLayout: ViewGroup?
        get() = mContentLayout

    protected val actionBarLayout: ViewGroup?
        get() = mActionBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setRootContentView()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        setRootContentView()
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)
        setRootContentView()
    }

    /**
     * Set a custom action bar layout. This method should be called before
     * [.setContentView]. If the custom layout contains a view with TAG
     * R.string.tag_ab_home, this view will behave like a home button in
     * the built-in action bar. If the custom layout contains a [TextView]
     * with ID R.id.text_ab_title, this view's content will be set to
     * the label of this activity (specified in Manifest.xml).
     *
     * @param layoutResId
     * resource ID of the action bar layout.
     * @param overlay
     * whether the action bar should be overlaid on the content view.
     */
    @JvmOverloads
    fun setActionBarView(layoutResId: Int, overlay: Boolean = false) {
        mActionBarLayoutId = layoutResId
        mActionBarOverlay = overlay
    }

    /**
     * Set the title text in the action bar.
     */
    fun setActionBarTitle(title: CharSequence?) {
        mTitleView!!.text = title
    }

    /**
     * Set root view for the activity and initialize the action bar.
     */
    private fun setRootContentView() {
        layoutInflater.inflate(mActionBarLayoutId, mActionBarLayout)
        if (!mActionBarOverlay) {
            val lp = mContentLayout.layoutParams as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.BELOW, R.id.layout_ab)
            mContentLayout.layoutParams = lp
        }

        // set up title
        val titleView = mActionBarLayout?.findViewById<View>(R.id.text_ab_title)
        if (titleView is TextView) {
            mTitleView = titleView
            try {
                val titleRes = packageManager.getActivityInfo(componentName, 0).labelRes
                val title = resources.getString(titleRes)
                setActionBarTitle(title)
            } catch (e: PackageManager.NameNotFoundException) {
                // leave the title empty
            } catch (e: Resources.NotFoundException) {
            }

        }
        mTopTitleDividerLineView = mActionBarLayout?.findViewById(R.id.view_top_title_divider_line)
        mMiddleTitleView = mActionBarLayout?.findViewById(R.id.text_ab_middle)
        mMiddleLayoutView = mActionBarLayout?.findViewById(R.id.layout_ab_middle)
        if (mMiddleTitleView != null) {
            mMiddleTitleView!!.setOnClickListener { onClickMiddleTitleView() }
        }
        mRightTitleView = mActionBarLayout?.findViewById(R.id.text_ab_right)
        mRightLayoutView = mActionBarLayout?.findViewById(R.id.layout_ab_right)
        mRightImageView = mActionBarLayout?.findViewById(R.id.image_right)
        if (mRightTitleView != null) {
            mRightTitleView!!.setOnClickListener { onClickRightTitleView() }
        }

        // listen to click on home
        val homeView = mActionBarLayout?.findViewWithTag<View>(getString(R.string.mercku_tag_ab_home))
        homeView?.setOnClickListener { onHomeSelected() }
    }

    /**
     * Default behavior for click on the title or the back button in the action
     * bar. Subclass may override this method to provide custom navigation.
     */
    protected open fun onHomeSelected() {
        finish()
    }

    protected fun onClickMiddleTitleView() {}

    protected open fun onClickRightTitleView() {}

    protected fun setMiddleTitleText(middleTitle: String?) {
        if (mMiddleTitleView != null) {
            mMiddleTitleView!!.text = middleTitle ?: ""
            mMiddleLayoutView!!.visibility = View.VISIBLE
        }
    }

    protected fun setLeftTitleText(title: String?) {
        if (mTitleView != null) {
            mTitleView!!.text = title ?: ""
            mTitleView!!.visibility = View.VISIBLE
        }
    }

    protected fun setLeftTitleImage(rightTitle: String?, drawableId: Int) {
        if (mTitleView != null) {
            mTitleView!!.text = rightTitle ?: ""
            mTitleView!!.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.mercku_margin_five)
            mTitleView!!.setCompoundDrawablesWithIntrinsicBounds(getDrawable(drawableId), null, null, null)
            mTitleView!!.visibility = View.VISIBLE
        }
    }

    protected fun setRightTitleText(rightTitle: String?) {
        if (mRightTitleView != null) {
            mRightTitleView!!.text = rightTitle ?: ""
            mRightLayoutView!!.visibility = View.VISIBLE
        }
    }

    protected fun setRightTitleImage(rightTitle: String?, drawableId: Int) {
        if (mRightTitleView != null) {
            mRightTitleView!!.text = rightTitle ?: ""
            mRightTitleView!!.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.mercku_margin_five)
            mRightTitleView!!.setCompoundDrawablesWithIntrinsicBounds(getDrawable(drawableId), null, null, null)
            mRightLayoutView!!.visibility = View.VISIBLE
        }
    }

    protected fun setRightTitleViewVisibility(isVisibility: Boolean) {
        mRightLayoutView!!.visibility = if (isVisibility) View.VISIBLE else View.GONE
    }

    protected fun setTopTitleDividerLineViewVisibility(isVisibility: Boolean) {
        mTopTitleDividerLineView!!.visibility = if (isVisibility) View.VISIBLE else View.GONE
    }

    protected fun setRightImageView(resId: Int, onClickListener: View.OnClickListener) {
        mRightImageView?.visibility = View.VISIBLE
        mRightImageView?.setImageResource(resId)
        mRightTitleView?.visibility = View.GONE
        mRightImageView?.setOnClickListener(onClickListener)
    }
}

