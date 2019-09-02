package com.mercku.ipsdemo.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mercku.ipsdemo.activity.DeleteModeActivity
import com.mercku.ipsdemo.activity.MainActivity
import com.mercku.ipsdemo.model.DeleteObj

abstract class BaseDeleteModeRecyclerAdapter(protected var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var mDataList: MutableList<DeleteObj> = mutableListOf()

    protected var mIsDeleteMode: Boolean = false

    protected var mSelectedList: MutableList<DeleteObj> = mutableListOf()

    val seletedList: List<DeleteObj>?
        get() = mSelectedList

    fun setDeleteMode(deleteMode: Boolean) {
        mIsDeleteMode = deleteMode
        for (obj in mDataList) {
            obj.isSelected = false
        }
        mSelectedList.clear()
        notifyDataSetChanged()
    }

    open fun setOnItemClickListener(clickListener: DeleteModeActivity) {
        mClickListener = clickListener
    }

    fun setOnItemLongClickListener(clickListener: MyLongClickListener) {
        mLongClickListener = clickListener
    }

    interface MyClickListener {

        fun onItemClick(position: Int, v: View)
    }

    interface MyLongClickListener {

        fun onItemLongClick(position: Int, v: View): Boolean
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    protected fun clearAllData() {
        mDataList.clear()
        mSelectedList.clear()
    }

    fun onClickItem(view: View, position: Int) {
        if (mIsDeleteMode) {
            onItemSeleted(view, position)
        } else {
            onItemDetail(view)
        }
    }

    private fun onItemSeleted(view: View, position: Int) {
        val deleteObj = mDataList[position]
        if (deleteObj.isSelected) {
            deleteObj.isSelected = false
            mSelectedList.remove(deleteObj)
        } else {
            deleteObj.isSelected = true
            mSelectedList.add(deleteObj)
        }
        notifyItemChanged(position)
        checkWhetherAllSelectedStatus()
    }

    fun onItemLongClicked(position: Int) {
        val deleteObj = mDataList[position]
        deleteObj.isSelected = true
        mSelectedList.add(deleteObj)
        notifyItemChanged(position)
        checkWhetherAllSelectedStatus()
    }

    private fun checkWhetherAllSelectedStatus() {
        if (mContext is DeleteModeActivity) {
            if (mSelectedList.size == mDataList.size) {
                (mContext as DeleteModeActivity).setSelectAll(true)
                (mContext as DeleteModeActivity).setDoneEnable(true)
            } else if (mSelectedList.size == 0) {
                (mContext as DeleteModeActivity).setSelectAll(false)
                (mContext as DeleteModeActivity).setDoneEnable(false)
            } else {
                (mContext as DeleteModeActivity).setSelectAll(false)
                (mContext as DeleteModeActivity).setDoneEnable(true)
            }
        }
    }

    abstract fun onItemDetail(view: View)

    fun selectAll() {
        mSelectedList.clear()
        for (i in mDataList.indices) {
            mDataList[i].isSelected = true
            mSelectedList.add(mDataList[i])
        }
        notifyDataSetChanged()
    }

    fun unselectAll() {
        for (i in mDataList.indices) {
            mDataList[i].isSelected = false
        }
        mSelectedList.clear()
        notifyDataSetChanged()
    }

    fun refreshListAfterDelete() {
        mIsDeleteMode = false
        mDataList.removeAll(mSelectedList)
        for (obj in mDataList) {
            obj.isSelected = false
        }
        mSelectedList.clear()
        notifyDataSetChanged()
    }

    open class DeleteObjViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            mClickListener?.onItemClick(adapterPosition, v)
        }

        override fun onLongClick(v: View): Boolean {
            return mLongClickListener?.onItemLongClick(adapterPosition, v) ?: false
        }
    }

    companion object {

        protected var mLongClickListener: MyLongClickListener? = null

        protected var mClickListener: MyClickListener? = null
    }
}