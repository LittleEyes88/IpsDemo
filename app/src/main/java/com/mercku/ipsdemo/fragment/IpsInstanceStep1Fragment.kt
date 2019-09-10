package com.mercku.ipsdemo.fragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mercku.ipsdemo.R

class IpsInstanceStep1Fragment : BaseFragment() {
    override fun initView(view: View) {
        val imageView = view.findViewById<ImageView>(R.id.image)
        imageView.setImageResource(R.drawable.img_example_01)
        val textView = view.findViewById<TextView>(R.id.text)
        textView.setText(R.string.upload_house_plan)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_ips_instance_step
    }
}