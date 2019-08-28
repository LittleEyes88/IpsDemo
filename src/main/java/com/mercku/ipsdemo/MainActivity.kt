package com.mercku.ipsdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewStub
import android.widget.TextView
import androidx.appcompat.view.ActionBarPolicy
import androidx.recyclerview.widget.RecyclerView
import com.mercku.base.ui.BaseContentActivity
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import android.R.attr.data
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat.getExtras
import java.io.File


class MainActivity : BaseContentActivity(), EasyPermissions.PermissionCallbacks {


    private lateinit var mAddNewHouseLayout: View
    private lateinit var mRecyclerView: RecyclerView
    private var mAddTextView: TextView? = null
    private var mNoContentLayout: ViewStub? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mAddNewHouseLayout = findViewById<View>(R.id.layout_new_house)
        showNoContentLayout(false)
    }

    public fun onClickAddNewHouse(view: View) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startChoosePictureActivityForResult()
        } else {
            EasyPermissions.requestPermissions(this, "Need permission to choose picture",
                    RequestConstants.REQUEST_CAMERA__STORAGE_PERMISSIONS, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    fun showNoContentLayout(isShow: Boolean) {
        if (isShow) {
            if (mNoContentLayout == null) {
                var viewstub = findViewById<ViewStub>(R.id.layout_no_content)
                viewstub.inflate()

                mNoContentLayout = viewstub.findViewById(R.id.inflated)
                mAddTextView = mNoContentLayout?.findViewById<TextView>(R.id.text_add)
                mAddTextView?.setOnClickListener { onClickAddNewHouse(it) }
            } else {
                mNoContentLayout!!.visibility = View.VISIBLE
            }
            mRecyclerView.visibility = View.GONE
            mAddNewHouseLayout.visibility = View.VISIBLE
        } else {
            mRecyclerView.visibility = View.VISIBLE
            mAddNewHouseLayout.visibility = View.VISIBLE

            mNoContentLayout?.visibility = View.GONE
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        AppSettingsDialog.Builder(this).setRationale(getString(R.string.merkcu_trans0269)).build().show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startChoosePictureActivityForResult()
    }

    private fun startChoosePictureActivityForResult() {
        var albumIntent = Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, RequestConstants.REQUEST_ALBUM);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                RequestConstants.REQUEST_ALBUM -> {
                    val bundle = data?.getExtras()
                    android.util.Log.d("ryq", "onActivityResult  bundle=" + bundle)
                    if (bundle != null) {
                        //  var bitmap = bundle!!.getParcelable ("data");

                    }
                    data?.data?.let {
                        var filePath=FileUtil.getPathFromUri(data!!.data!!, this@MainActivity)
                        var intent = Intent(this, AddLocatorActivity::class.java)
                        intent.putExtra(ExtraConstants.EXTRA_FILE_PATH, filePath)
                        startActivity(intent)
                    }


                }
            }
        }
    }
}
