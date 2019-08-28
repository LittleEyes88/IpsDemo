package com.mercku.ipsdemo

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocatorAdapter(val mContext: Context, private val mData: ArrayList<IpsLocator>, val mOnItemClickListener: OnItemClickListener) : RecyclerView.Adapter<LocatorAdapter.LocatorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocatorViewHolder {
        var rootView = LayoutInflater.from(mContext).inflate(R.layout.cell_locator, parent, false)
        var holder = LocatorViewHolder(rootView)
        rootView.setOnClickListener {
            mOnItemClickListener.onItemClick(holder.adapterPosition, rootView.id)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: LocatorViewHolder, position: Int) {
        if (position > RecyclerView.NO_POSITION) {
            var ipsLocator = mData[position]
            when (ipsLocator.mType) {
                TypeConstants.TYPE_M3 -> {
                    holder.mLocatorImageView.setImageResource(R.drawable.img_m3_blank)

                }
                TypeConstants.TYPE_Bee -> {
                    holder.mLocatorImageView.setImageResource(R.drawable.img_bee_blank)

                }
            }
            holder.mLocatorImageView.isSelected = ipsLocator.mIsSelected
            holder.mLocatorCheckedImageView.visibility = if (ipsLocator.mIsAdded) {
                View.VISIBLE
            } else {
                View.GONE
            }
            holder.mLocatorTextView.text = ipsLocator.mName
        }

    }

    class LocatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mLocatorImageView: ImageView = itemView.findViewById(R.id.image_locator)
        var mLocatorCheckedImageView: ImageView = itemView.findViewById(R.id.image_locator_checked)
        var mLocatorTextView: TextView = itemView.findViewById(R.id.text_locator)
    }
}
