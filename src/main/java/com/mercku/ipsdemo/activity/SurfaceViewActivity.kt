package com.mercku.ipsdemo.activity

import android.os.Bundle
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.view.MySurfaceView
import android.view.SurfaceView


/**
 * Created by yanqiong.ran on 2019-08-31.
 */
class SurfaceViewActivity:BaseContentActivity(){
    private lateinit var mSurfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_surface_view)
        //mSurfaceView=findViewById(R.id.layout_surface_view)
       // mSurfaceView.setZOrderOnTop(true);
        //mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }
}