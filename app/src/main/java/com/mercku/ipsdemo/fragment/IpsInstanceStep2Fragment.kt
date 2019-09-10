package com.mercku.ipsdemo.fragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mercku.ipsdemo.R

class IpsInstanceStep2Fragment : BaseFragment() {
    override fun initView(view: View) {
        val imageView = view.findViewById<ImageView>(R.id.image)
        imageView.setImageResource(R.drawable.img_example_02)
        val textView = view.findViewById<TextView>(R.id.text)
        textView.setText(R.string.drag_locator)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_ips_instance_step
    }
}