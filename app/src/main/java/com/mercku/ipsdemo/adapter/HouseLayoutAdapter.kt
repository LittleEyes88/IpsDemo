package com.mercku.ipsdemo.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.activity.HouseLayoutDetailActivity
import com.mercku.ipsdemo.activity.IpsInstanceActivity
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.util.CacheUtil
import java.io.File

class HouseLayoutAdapter(mContext: Context)
    : BaseDeleteModeRecyclerAdapter(mContext) {
    private var mEditText: EditText? = null
    private var mAlertDialog: AlertDialog? = null

    fun setDataList(datas: ArrayList<IpsHouse>?) {

        clearAllData()
        if (datas != null && datas.size != 0) {
            mDataList.addAll(datas)
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        var rootView = LayoutInflater.from(mContext).inflate(R.layout.cell_house_layout, parent, false)
        var holder = HouseViewHolder(rootView)
        /*rootView.setOnClickListener {
            android.util.Log.d("ryq", "onCreateViewHolder holder.adapterPosition=" + holder.adapterPosition + " rootView.id=" + rootView.id)
            mOnItemClickListener.onItemClick(holder.adapterPosition, rootView.id)
        }*/
        holder.mEditNameImageView.setOnClickListener {
            if (holder.adapterPosition > RecyclerView.NO_POSITION) {
                onClickEditHouseName(holder.adapterPosition, it)
            }

        }
        return holder
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        var holder = viewHolder as HouseViewHolder
        if (position > RecyclerView.NO_POSITION) {
            var ipsHouse = mDataList[position] as IpsHouse
            android.util.Log.d("ryq", "ipsHouse=" + ipsHouse.mImageFilePath)
            var file = File(ipsHouse.mImageFilePath)
            if (file.exists()) {
                var uri = Uri.fromFile(file)
                var bitmap = BitmapFactory.decodeStream(
                        mContext.contentResolver.openInputStream(uri))
                holder.mLayoutImageView.setImageBitmap(bitmap)
            } else {
                holder.mLayoutImageView.setImageResource(R.drawable.img_house_plan)
            }

            holder.mNameTextView.text = ipsHouse.mName
            holder.mNameTextView.tag = ipsHouse

            if (mIsDeleteMode) {
                viewHolder.mCheckImageView.visibility = View.VISIBLE
                viewHolder.mCheckImageView.isSelected = ipsHouse.isSelected
            } else {
                viewHolder.mCheckImageView.visibility = View.GONE
            }

            viewHolder.mEditNameImageView.visibility = if (TextUtils.isEmpty(ipsHouse.mId) &&
                    TextUtils.isEmpty(ipsHouse.mImageFilePath)) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }


    fun onClickEditHouseName(position: Int, view: View) {

        var ipsHouse = mDataList[position] as IpsHouse
        var builder = AlertDialog.Builder(mContext)

        builder.setView(R.layout.dialog_rename)
        var root = LayoutInflater.from(mContext).inflate(R.layout.dialog_rename, null, false)
        mEditText = root.findViewById<EditText>(R.id.edit_text_name)
        mEditText?.setText(ipsHouse.mName, null)
        ipsHouse.mName?.let {
            mEditText?.setSelection(ipsHouse.mName!!.length)
        }

        root.setTag(ipsHouse)
        builder.setView(root)
        mAlertDialog = builder.create();
        mAlertDialog?.show()
    }

    fun onClickCancel(view: View) {
        mAlertDialog?.dismiss()
    }

    fun onClickConfim(view: View) {

        var parent = view.parent as ViewGroup
        if (parent.tag != null && parent.tag is IpsHouse) {
            val ipsHouse = parent.tag as IpsHouse
            mEditText?.let {
                ipsHouse.mName = mEditText!!.text.toString()
                CacheUtil.updateSomeHouse(ipsHouse, mContext)
            }
        }
        mAlertDialog?.dismiss()

        notifyDataSetChanged()
    }

    override fun onItemDetail(view: View) {
        if (view.findViewById<View>(R.id.text_house_name).tag !is IpsHouse) {
            return
        }
        val ipsHouse = view.findViewById<View>(R.id.text_house_name).tag as IpsHouse
        if (TextUtils.isEmpty(ipsHouse.mId) && TextUtils.isEmpty(ipsHouse.mImageFilePath)) {
            mContext.startActivity(Intent(mContext, IpsInstanceActivity::class.java))
        } else {
            val intent = Intent(mContext, HouseLayoutDetailActivity::class.java)
            intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, ipsHouse)
            mContext.startActivity(intent)
        }
    }

    class HouseViewHolder(itemView: View) : DeleteObjViewHolder(itemView) {
        var mLayoutImageView: ImageView = itemView.findViewById(R.id.image_layout)
        var mEditNameImageView: ImageView = itemView.findViewById(R.id.image_edit_house_name)
        var mCheckImageView: ImageView = itemView.findViewById(R.id.image_checkbox)
        var mNameTextView: TextView = itemView.findViewById(R.id.text_house_name)
    }
}
