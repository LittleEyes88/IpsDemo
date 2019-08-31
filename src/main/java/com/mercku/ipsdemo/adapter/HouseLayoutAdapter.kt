package com.mercku.ipsdemo.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mercku.ipsdemo.model.IpsLocator
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.TypeConstants
import com.mercku.ipsdemo.listener.OnItemClickListener
import com.mercku.ipsdemo.model.IpsHouse
import java.io.File

class HouseLayoutAdapter(val mContext: Context, private val mData: ArrayList<IpsHouse>,
                         val mOnItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<HouseLayoutAdapter.HouseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        var rootView = LayoutInflater.from(mContext).inflate(R.layout.cell_house_layout, parent, false)
        var holder = HouseViewHolder(rootView)
        rootView.setOnClickListener {
            android.util.Log.d("ryq", "onCreateViewHolder holder.adapterPosition=" + holder.adapterPosition + " rootView.id=" + rootView.id)
            mOnItemClickListener.onItemClick(holder.adapterPosition, rootView.id)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        if (position > RecyclerView.NO_POSITION) {
            var ipsHouse = mData[position]
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
        }

    }

    fun onClickEditHouseName(view: View) {
        if (view.tag != null && view.tag is IpsHouse) {
            var ipsHouse = view.tag as IpsHouse
            android.util.Log.d("ryq", "onClickEditHouseName ipsHouse=" + ipsHouse)
        }
    }

    class HouseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mLayoutImageView: ImageView = itemView.findViewById(R.id.image_layout)
        var mEditNameImageView: ImageView = itemView.findViewById(R.id.image_edit_house_name)
        var mNameTextView: TextView = itemView.findViewById(R.id.text_house_name)
    }
}
