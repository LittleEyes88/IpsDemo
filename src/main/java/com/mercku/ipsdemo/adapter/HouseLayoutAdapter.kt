package com.mercku.ipsdemo.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.activity.HouseLayoutDetailActivity
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.model.IpsHouse
import java.io.File

class HouseLayoutAdapter(mContext: Context )
    : BaseDeleteModeRecyclerAdapter(mContext) {
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
                        mContext.getContentResolver().openInputStream(uri))
                holder.mLayoutImageView.setImageBitmap(bitmap)
            }

            holder.mNameTextView.text = ipsHouse.mName
            holder.mNameTextView.tag = ipsHouse

            if (mIsDeleteMode) {
                viewHolder.mCheckImageView.visibility = View.VISIBLE
                viewHolder.mCheckImageView.isSelected = ipsHouse.isSelected
            } else {
                viewHolder.mCheckImageView.visibility = View.GONE
            }

        }
    }


    fun onClickEditHouseName(view: View) {
        if (view.tag != null && view.tag is IpsHouse) {
            var ipsHouse = view.tag as IpsHouse
            android.util.Log.d("ryq", "onClickEditHouseName ipsHouse=" + ipsHouse)
        }
    }

    override fun onItemDetail(view: View) {
        if (view.findViewById<View>(R.id.text_house_name).tag !is IpsHouse) {
            return
        }
        val ipsHouse = view.findViewById<View>(R.id.text_house_name).tag as IpsHouse

        var intent = Intent(mContext, HouseLayoutDetailActivity::class.java)
        intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, ipsHouse)
        mContext.startActivity(intent)

    }

    class HouseViewHolder(itemView: View) : DeleteObjViewHolder(itemView) {
        var mLayoutImageView: ImageView = itemView.findViewById(R.id.image_layout)
        var mEditNameImageView: ImageView = itemView.findViewById(R.id.image_edit_house_name)
        var mCheckImageView: ImageView = itemView.findViewById(R.id.image_checkbox)
        var mNameTextView: TextView = itemView.findViewById(R.id.text_house_name)
    }
}
