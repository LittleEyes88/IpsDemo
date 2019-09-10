package com.mercku.ipsdemo.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.mercku.ipsdemo.R

class MyFamilyActivity : BaseContentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_family)
        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}