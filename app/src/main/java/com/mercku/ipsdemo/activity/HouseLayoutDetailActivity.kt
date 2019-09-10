package com.mercku.ipsdemo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.model.IpsHouse

class HouseLayoutDetailActivity : BaseContentActivity() {
    private lateinit var mCustomView: com.mercku.ipsdemo.view.MyLocatorView
    private var mIpsHouse: IpsHouse? = null
    private lateinit var mSaveSuccessLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_layout_detail)
        setRightTitleText(getString(R.string.family_list))
        mCustomView = findViewById(R.id.layout_custom_view)
        mSaveSuccessLayout = findViewById(R.id.layout_save_success)
        if (intent.getBooleanExtra(ExtraConstants.EXTRA_IS_SAVE, false)) {
            mSaveSuccessLayout.visibility = View.VISIBLE
        }

        mIpsHouse = intent.getParcelableExtra<IpsHouse>(ExtraConstants.EXTRA_HOUSE_DETAIL)
        if (mIpsHouse == null) {
            return
        }
        mCustomView.setHouseDetail(mIpsHouse)
    }

    override fun onClickRightTitleView() {
        var intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    public fun onClickSearch(view: View) {
        var intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, mIpsHouse)
        startActivity(intent)
    }
}
