package com.mercku.ipsdemo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.R

class SearchResultActivity : BaseContentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        setRightTitleText(getString(R.string.family_list))
    }

    override fun onClickRightTitleView() {
        //todo,return to mainactivity
        var intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


}
