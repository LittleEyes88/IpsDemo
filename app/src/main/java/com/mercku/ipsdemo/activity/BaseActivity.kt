package com.mercku.ipsdemo.activity

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.constants.PermissionConstants
import com.mercku.ipsdemo.util.ActivityUtils


abstract class BaseActivity : AppCompatActivity() {

    protected var mActionBarLayout: ViewGroup? = null

    lateinit var mContentLayout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    override fun setContentView(layoutResID: Int) {
        setRootContentView()
        layoutInflater.inflate(layoutResID, mContentLayout)
    }

    override fun setContentView(view: View) {
        setRootContentView()
        mContentLayout.addView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        setRootContentView()
        mContentLayout.addView(view, params)
    }

    /**
     * Set root view for the activity
     */
    private fun setRootContentView() {
        super.setContentView(R.layout.activity_base)
        mContentLayout = findViewById(R.id.layout_content)
        mActionBarLayout = findViewById(R.id.layout_ab)
    }


/*
    override fun onBackPressed() {
        if (DialogUtil.needDismissLoading()) {
            DialogUtil.dismissLoadingPopup()
        } else {
            super.onBackPressed()
        }
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionConstants.MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                onStoragePermissionGranted()
            } else {
                Toast.makeText(this, resources.getText(R.string.mercku_trans0270), Toast.LENGTH_LONG).show()
            }
            PermissionConstants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                onCameraPermissionGranted()
            } else {
                Toast.makeText(this, resources.getText(R.string.merkcu_trans0269), Toast.LENGTH_LONG).show()
            }
            PermissionConstants.MY_PERMISSIONS_REQUEST_INSTALL_PACKAGE -> if (grantResults.isNotEmpty() && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                onInstallPackagePermissionGranted()
            } else {
                onDenyInstallPackagePermissionGranted()
            }
            PermissionConstants.MY_PERMISSIONS_REQUEST_LOCATION -> if (grantResults.isNotEmpty() && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                onLocationPermissionGranted()
            } else {
                onLocationPermissionDenied()
            }
            else -> {
            }
        }
    }

    protected open fun onLocationPermissionGranted() {}

    protected open fun onLocationPermissionDenied() {}

    protected open fun onStoragePermissionGranted() {}

    protected open fun onCameraPermissionGranted() {}

    protected open fun onInstallPackagePermissionGranted() {}

    protected open fun onDenyInstallPackagePermissionGranted() {}

}
