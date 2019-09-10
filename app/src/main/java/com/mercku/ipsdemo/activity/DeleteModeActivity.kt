package com.mercku.ipsdemo.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mercku.ipsdemo.adapter.BaseDeleteModeRecyclerAdapter
import com.mercku.ipsdemo.listener.RefreshCompleteListener

/**
 * Created by Administrator on 2018/4/24.
 */

abstract class DeleteModeActivity : BaseContentActivity(), RefreshCompleteListener, BaseDeleteModeRecyclerAdapter.MyLongClickListener, BaseDeleteModeRecyclerAdapter.MyClickListener {

    private var mDeleteModeLayout: View? = null

    protected var mBackTextView: TextView? = null

    private var mAllTextView: TextView? = null

    protected var mMiddleTitleTextView: TextView? = null

    protected var mRightTitleTextView: TextView? = null

    protected lateinit var mDoneTextView: TextView

    protected var mTopHintTextView: TextView? = null

    protected lateinit var mRecyclerView: RecyclerView

    protected var mReloadLayout: ViewGroup? = null

    protected lateinit var mNoContentLayout: ViewGroup

    private var mAddTextView: TextView? = null

    protected var mOperationTextView: TextView? = null

    protected var mIsEditMode: Boolean = false

    protected var mIsAllSelect: Boolean = false

    private var mDeleteModeRecyclerAdapter: BaseDeleteModeRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        setContentView(R.layout.router_activity_delete_mode)
        mDeleteModeLayout = findViewById(R.id.layout_delete_mode)
        mBackTextView = findViewById(R.id.text_back)
        mAllTextView = findViewById(R.id.text_all)
        mMiddleTitleTextView = findViewById(R.id.text_middle_title)
        mRightTitleTextView = findViewById(R.id.text_right_title)
        mTopHintTextView = findViewById(R.id.text_top_hint)
        mRecyclerView = findViewById(R.id.recycle_list)
        mReloadLayout = findViewById(R.id.layout_network_error)
        mNoContentLayout = findViewById(R.id.layout_no_content)
        mAddTextView = findViewById(R.id.text_add)
        mOperationTextView = findViewById(R.id.text_operation)
        mDoneTextView = findViewById(R.id.text_done)

        mBackTextView?.setOnClickListener { onClickBack() }
        mRightTitleTextView?.setOnClickListener { onClickRightTitleView() }
        mAllTextView?.setOnClickListener { onClickAll() }
        mAddTextView?.setOnClickListener { onClickAdd() }
        mDoneTextView?.setOnClickListener { onClickDone() }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        mRecyclerView!!.layoutManager = layoutManager

        initAdapter()
        initView()*/
    }

    internal abstract fun initData()

    protected open fun initView() {
/*        mAllTextView!!.visibility = View.GONE
        mDoneTextView!!.text = getText(R.string.router_trans0035)
        mDoneTextView!!.visibility = View.GONE
        mRightTitleTextView!!.text = getText(R.string.router_trans0033)
        mRightTitleTextView!!.visibility = View.GONE
        mMiddleTitleTextView!!.visibility = View.GONE
        mTopHintTextView!!.visibility = View.GONE*/
    }

    internal abstract fun initAdapter()

    protected fun setAdapter(deleteModeRecyclerAdapter: BaseDeleteModeRecyclerAdapter?) {
        mDeleteModeRecyclerAdapter = deleteModeRecyclerAdapter
        mRecyclerView!!.adapter = deleteModeRecyclerAdapter
        deleteModeRecyclerAdapter?.setOnItemClickListener(this)
        deleteModeRecyclerAdapter?.setOnItemLongClickListener(this)
    }

    open fun onClickBack() {
        if (!mIsEditMode) {
            finish()
        }
    }

    override fun onClickRightTitleView() {
        mIsAllSelect = false
        if (!mIsEditMode) {
            initEditMode()
        } else {
            exitEditMode()
            if (mDeleteModeRecyclerAdapter != null) {
                mDeleteModeRecyclerAdapter!!.setDeleteMode(false)
            }
        }
    }

    protected open fun initEditMode() {
/*        mIsEditMode = true
        if (mDeleteModeRecyclerAdapter != null) {
            mDeleteModeRecyclerAdapter!!.setDeleteMode(true)
        }
        mRightTitleTextView!!.text = getText(R.string.router_trans0025)
        mRightTitleTextView!!.visibility = if (mDeleteModeRecyclerAdapter!!.itemCount > 0) View.VISIBLE else View.GONE
        mAllTextView!!.visibility = View.VISIBLE
        mDoneTextView!!.text = getText(R.string.router_trans0033)
        mDoneTextView!!.isEnabled = false
        mDoneTextView!!.visibility = View.VISIBLE
        mBackTextView!!.visibility = View.GONE
        mMiddleTitleTextView!!.visibility = View.VISIBLE*/
    }

    protected open fun exitEditMode() {
/*        mIsEditMode = false
        mRightTitleTextView!!.text = getText(R.string.router_trans0033)
        mRightTitleTextView!!.visibility = if (mDeleteModeRecyclerAdapter!!.itemCount > 0) View.VISIBLE else View.GONE
        mAllTextView!!.visibility = View.GONE
        mAllTextView!!.isSelected = false
        mDoneTextView!!.text = getText(R.string.router_trans0035)
        mDoneTextView!!.isEnabled = true
        mDoneTextView!!.visibility = View.VISIBLE
        mBackTextView!!.visibility = View.VISIBLE
        mMiddleTitleTextView!!.visibility = View.GONE*/
    }

    private fun onClickAll() {
        if (mDeleteModeRecyclerAdapter == null) {
            return
        }
        mIsAllSelect = !mIsAllSelect
        mAllTextView!!.isSelected = mIsAllSelect
        if (mIsAllSelect) {
            mDeleteModeRecyclerAdapter!!.selectAll()
            mDoneTextView!!.isEnabled = true
            mDoneTextView!!.visibility = View.VISIBLE
        } else {
            mDeleteModeRecyclerAdapter!!.unselectAll()
            mDoneTextView!!.isEnabled = false
            mDoneTextView!!.visibility = View.VISIBLE
        }
    }

    fun onClickDone() {
        if (mIsEditMode) {
            onClickDelete(this)
        } else {
            onClickAdd()
        }
    }

    open fun onClickAdd() {
    }

    protected open fun onClickDelete(listener: RefreshCompleteListener) {}

    override fun onItemClick(position: Int, view: View) {
        mDeleteModeRecyclerAdapter!!.onClickItem(view, position)
    }

    override fun onItemLongClick(position: Int, view: View): Boolean {
        if (!mIsEditMode) {
            initEditMode()
            mDoneTextView!!.isEnabled = true
            mDeleteModeRecyclerAdapter!!.onItemLongClicked(position)
            return true
        }
        return false
    }

    open fun setSelectAll(isAllSelected: Boolean) {
        mIsAllSelect = isAllSelected
        mAllTextView!!.isSelected = isAllSelected
    }

    override fun onRefreshCompleteListener(errorCode: Int?, isManualRefresh: Boolean) {
        // 0代表刷新成功,否则刷新失败展示刷新失败弹窗
        /*      if (errorCode == null || errorCode != 0) {
                  setReloadLayoutVisibility(true)
                  mRecyclerView!!.visibility = View.GONE
                  mDoneTextView!!.visibility = View.GONE
                  mTopHintTextView!!.visibility = View.GONE
              } else {
                  setReloadLayoutVisibility(false)
                  setWebsiteBlackHintViewVisibility(true)
                  if (mDeleteModeRecyclerAdapter == null || mDeleteModeRecyclerAdapter!!.itemCount == 0) {
                      setNoContentLayoutVisibility(true)
                      mDoneTextView!!.visibility = View.GONE
                      mRecyclerView!!.visibility = View.GONE
                  } else {
                      setNoContentLayoutVisibility(false)
                      mRecyclerView!!.visibility = View.VISIBLE
                      mDoneTextView!!.visibility = View.VISIBLE
                  }
              }
              mRightTitleTextView!!.visibility = if (mDeleteModeRecyclerAdapter != null && mDeleteModeRecyclerAdapter!!.itemCount > 0)
                  View.VISIBLE
              else
                  View.GONE*/
    }

    // 该view只有在ParentControlBlackListActivity时才有可能显示，其他页面均不显示
    protected open fun setWebsiteBlackHintViewVisibility(isVisible: Boolean) {}

    fun setReloadLayoutVisibility(isVisible: Boolean) {
        /* if (isVisible) {
             mReloadLayout!!.visibility = View.VISIBLE
             val reloadTextView = mReloadLayout!!.findViewById<TextView>(R.id.text_reload)
             reloadTextView.setOnClickListener {
                 mReloadLayout!!.visibility = View.GONE
                 initData()
             }
         } else {
             mReloadLayout!!.visibility = View.GONE
         }*/
    }

    fun setNoContentLayoutVisibility(isVisible: Boolean) {
        if (isVisible) {
            mNoContentLayout!!.visibility = View.VISIBLE
            mAddTextView!!.visibility = View.VISIBLE
        } else {
            mNoContentLayout!!.visibility = View.GONE
            mAddTextView!!.visibility = View.GONE
        }
    }

    fun setDoneEnable(doneEnable: Boolean) {
        if (mIsEditMode) {
            mDoneTextView!!.isEnabled = doneEnable
        }
    }
}
