package com.mercku.ipsdemo.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mercku.base.ui.BaseContentActivity
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.util.FileUtil
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.adapter.BaseDeleteModeRecyclerAdapter
import com.mercku.ipsdemo.adapter.HouseLayoutAdapter
import com.mercku.ipsdemo.constants.RequestConstants
import com.mercku.ipsdemo.listener.OnItemClickListener
import com.mercku.ipsdemo.listener.RefreshCompleteListener
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.util.AcacheUtil
import com.mercku.ipsdemo.util.CacheUtil
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : DeleteModeActivity(), EasyPermissions.PermissionCallbacks {
    override fun initData() {

    }

    override fun initAdapter() {

    }

    //private lateinit var mDoneText  View: TextView
    private lateinit var mHouseLayoutAdapter: HouseLayoutAdapter
    private lateinit var mData: ArrayList<IpsHouse>
    private lateinit var mAddNewHouseLayout: View
    // private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAddTextView: TextView

    //private lateinit var mNoContentLayout: ViewGroup
    //protected var mIsEditMode: Boolean = false
    //protected var mIsAllSelect: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mData = ArrayList<IpsHouse>()

        mHouseLayoutAdapter = HouseLayoutAdapter(this)

        setAdapter(mHouseLayoutAdapter)
        mAddNewHouseLayout = findViewById<View>(R.id.layout_new_house)

        mDoneTextView = findViewById(R.id.text_done)
        mNoContentLayout = findViewById(R.id.layout_no_content)
        mAddTextView = mNoContentLayout.findViewById<TextView>(R.id.text_add)
        mAddTextView.setOnClickListener { onClickAdd(it) }
        exitEditMode()

        mDoneTextView?.setOnClickListener { onClickDone() }
    }

    override fun onResume() {
        super.onResume()
        var acache = AcacheUtil.get(this)
        var cachedData: String? = acache.getAsString(ExtraConstants.EXTRA_HOUSE_LIST)
        if (cachedData != null) {
            var houseList = Gson().fromJson<ArrayList<IpsHouse>>(cachedData, object : TypeToken<ArrayList<IpsHouse>>() {}.type)
            if (!houseList.isEmpty()) {
                mData = houseList
                mHouseLayoutAdapter.setDataList(mData)
                if (mIsEditMode) {

                } else {
                    exitEditMode()

                }

            }

        }

    }

    override fun onHomeSelected() {
        if (mIsEditMode) {
            //exitEditMode()
            mTitleView?.let {
                it.isSelected = !it.isSelected
                if (it.isSelected) {
                    mHouseLayoutAdapter.selectAll()
                } else {
                    mHouseLayoutAdapter.unselectAll()
                }
            }
        } else {
            finish()
        }
    }

    override fun onClickDelete(listener: RefreshCompleteListener) {
        mHouseLayoutAdapter.seletedList?.let { mData.removeAll(it) }
        mHouseLayoutAdapter.setDataList(mData)
        CacheUtil.saveHouseList(mData, this)
        if (mData == null || mData.isEmpty()) {
            exitEditMode()
        }
    }

    override fun setSelectAll(isAllSelected: Boolean) {
        mIsAllSelect = isAllSelected
        mTitleView?.isSelected = isAllSelected
    }

    override fun onClickRightTitleView() {
        mIsAllSelect = false
        if (!mIsEditMode) {
            initEditMode()
        } else {
            exitEditMode()

        }
    }

    override fun exitEditMode() {
        mIsEditMode = false
        if (mHouseLayoutAdapter != null) {
            mHouseLayoutAdapter!!.setDeleteMode(false)
        }
        setRightTitleText(if (mData.size <= 0) "" else {
            getText(R.string.edit).toString()
        })
        if (mTitleView != null) {
            mTitleView!!.isSelected = false
        }
        setLeftTitleImage("", R.drawable.ic_back)

        setMiddleTitleText(getString(R.string.my_family))

        mDoneTextView.visibility = View.GONE

        showNoContentLayout(mData.size <= 0)
    }

    override fun initEditMode() {
        mIsEditMode = true
        if (mHouseLayoutAdapter != null) {
            mHouseLayoutAdapter!!.setDeleteMode(true)
        }
        mAddNewHouseLayout.visibility = View.GONE
        setRightTitleText(getText(R.string.cancel).toString())

        setLeftTitleImage(getString(R.string.select_all), R.drawable.ic_checkbox)
        setMiddleTitleText(getString(R.string.my_family))

        mDoneTextView.visibility = View.VISIBLE
    }

/*    override fun onItemClick(position: Int, viewId: Int) {
        if (position < mData.size) {
            var ipsHouse = mData[position]
            var intent = Intent(this, HouseLayoutDetailActivity::class.java)
            intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, ipsHouse)
            startActivity(intent)
        }
    }*/


    public fun onClickAdd(view: View) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startChoosePictureActivityForResult()
        } else {
            EasyPermissions.requestPermissions(this, "Need permission to choose picture",
                    RequestConstants.REQUEST_CAMERA__STORAGE_PERMISSIONS, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    fun showNoContentLayout(isShow: Boolean) {
        if (isShow) {

            mNoContentLayout!!.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            mAddNewHouseLayout.visibility = View.GONE
        } else {
            mRecyclerView.visibility = View.VISIBLE
            mAddNewHouseLayout.visibility = View.VISIBLE
            mNoContentLayout.visibility = View.GONE
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
                        var filePath = FileUtil.getPathFromUri(data.data, this@MainActivity)
                        var intent = Intent(this, AddLocatorActivity::class.java)
                        intent.putExtra(ExtraConstants.EXTRA_FILE_PATH, filePath)
                        startActivity(intent)
                    }


                }
            }
        }
    }
}
