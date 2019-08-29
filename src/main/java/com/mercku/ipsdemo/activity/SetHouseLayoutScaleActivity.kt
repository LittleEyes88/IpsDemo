package com.mercku.ipsdemo.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.ExtraConstants
import java.io.File

/**
 * Created by yanqiong.ran on 2019-08-29.
 */
class SetHouseLayoutScaleActivity : BaseContentActivity() {

    private lateinit var mCustomView: com.mercku.ipsdemo.view.MyHouseView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_house_layout_scale)
        setRightTitleText(getString(R.string.save))
        mCustomView = findViewById(R.id.layout_custom_view)
        var filePath = intent.getStringExtra(ExtraConstants.EXTRA_FILE_PATH)
        var file = File(filePath)
        if (file.exists()) {
            var uri = Uri.fromFile(file)
            var bitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri))
            android.util.Log.d("ryq", "SetHouseLayoutScaleActivity  bitmap=" + bitmap)
            mCustomView.setImageBitmap(bitmap)
        }
    }

    override fun onClickRightTitleView() {
        //todo save the layout
    }
}