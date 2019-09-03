package com.mercku.ipsdemo.adapter

import android.content.Context
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

class LocatorAdapter(val mContext: Context, private val mData: ArrayList<IpsLocator>, val mOnItemClickListener: OnItemClickListener) : RecyclerView.Adapter<LocatorAdapter.LocatorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocatorViewHolder {
        var rootView = LayoutInflater.from(mContext).inflate(R.layout.cell_locator, parent, false)
        var holder = LocatorViewHolder(rootView)
        rootView.setOnClickListener {
            android.util.Log.d("ryq", "onCreateViewHolder holder.adapterPosition=" + holder.adapterPosition + " rootView.id=" + rootView.id)
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
            android.util.Log.d("ryq", "onBindViewHolder ipsLocator.mType=" + ipsLocator.mType + " ipsLocator.mIsSelected=" + ipsLocator.mIsSelected
                    + " ipsLocator.mIsAdded=" + ipsLocator.mIsAdded)
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
