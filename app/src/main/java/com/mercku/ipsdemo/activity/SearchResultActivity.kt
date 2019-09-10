package com.mercku.ipsdemo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.model.IpsHouse

class SearchResultActivity : BaseContentActivity() {
    private lateinit var mCustomView: com.mercku.ipsdemo.view.MyStickerView
    private var mBitmap: Bitmap? = null
    private var mIpsHouse: IpsHouse? = null
    private lateinit var mHouseImageView: ImageView
    private lateinit var mHouseLayout: ViewGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        setRightTitleText(getString(R.string.family_list))


        //mHouseLayout = findViewById(R.id.house_layout)
        //mHouseImageView = findViewById<ImageView>(R.id.image_house)
        mCustomView = findViewById(R.id.layout_custom_view)


        mIpsHouse = intent.getParcelableExtra<IpsHouse>(ExtraConstants.EXTRA_HOUSE_DETAIL)
        if (mIpsHouse == null) {
            return
        }

        mCustomView.setHouseDetail(mIpsHouse)
    }

    override fun onClickRightTitleView() {
        //todo,return to mainactivity
        var intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


}
