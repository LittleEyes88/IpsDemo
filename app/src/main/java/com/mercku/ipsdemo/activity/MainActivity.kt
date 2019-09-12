package com.mercku.ipsdemo.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mercku.ipsdemo.R
import com.mercku.ipsdemo.adapter.HouseLayoutAdapter
import com.mercku.ipsdemo.constants.ExtraConstants
import com.mercku.ipsdemo.constants.RequestConstants
import com.mercku.ipsdemo.listener.OnItemClickListener
import com.mercku.ipsdemo.listener.RefreshCompleteListener
import com.mercku.ipsdemo.model.IpsHouse
import com.mercku.ipsdemo.util.AcacheUtil
import com.mercku.ipsdemo.util.CacheUtil
import com.mercku.ipsdemo.util.FileUtil
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : DeleteModeActivity(), EasyPermissions.PermissionCallbacks, OnItemClickListener {
    override fun initData() {

    }

    override fun initAdapter() {

    }

    //private lateinit var mDoneText  View: TextView
    private lateinit var mHouseLayoutAdapter: HouseLayoutAdapter
    private lateinit var mData: ArrayList<IpsHouse>
    private lateinit var mAddNewHouseButton: Button
    private lateinit var mViewStub: ViewStub
    private lateinit var mAddTextView: TextView
    // private lateinit var mRecyclerView: RecyclerView

    //protected var mIsEditMode: Boolean = false
    //protected var mIsAllSelect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewStub = findViewById(R.id.view_stub_empty)
        mViewStub.setOnInflateListener { stub, inflated ->
            mAddTextView = inflated.findViewById(R.id.text_add)
            mAddTextView.setOnClickListener {
                onClickAdd(mAddTextView)
            }
        }
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mHouseLayoutAdapter = HouseLayoutAdapter(this)
        mHouseLayoutAdapter.setOnItemClickListener(this)

        setAdapter(mHouseLayoutAdapter)
        mAddNewHouseButton = findViewById(R.id.btn_new_house)

        mDoneTextView = findViewById(R.id.text_done)
        mDoneTextView.setOnClickListener { onClickDone() }
    }

    override fun onResume() {
        super.onResume()
        val aCache = AcacheUtil.get(this)
        val cachedData: String? = aCache.getAsString(ExtraConstants.EXTRA_HOUSE_LIST)
        var houseList: ArrayList<IpsHouse>? = null
        if (cachedData != null) {
            houseList = Gson().fromJson<ArrayList<IpsHouse>>(cachedData, object : TypeToken<ArrayList<IpsHouse>>() {}.type)
        }
        mData = if (houseList == null || houseList.isEmpty()) {
            mViewStub.visibility = View.VISIBLE
            mAddNewHouseButton.visibility = View.GONE
            mRecyclerView.visibility = View.GONE
            arrayListOf()
        } else {
            mViewStub.visibility = View.GONE
            mAddNewHouseButton.visibility = View.VISIBLE
            mRecyclerView.visibility = View.VISIBLE

            mIsEditMode = false
            mHouseLayoutAdapter.setDeleteMode(false)
            setRightTitleText(getText(R.string.edit).toString())
            mTitleView?.isSelected = false

            houseList
        }

        mHouseLayoutAdapter.setDataList(mData)
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
        if (mData.isEmpty()) {
            exitEditMode()
            mViewStub.visibility = View.VISIBLE
            mAddNewHouseButton.visibility = View.GONE
            mRecyclerView.visibility = View.GONE
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
        mHouseLayoutAdapter.setDeleteMode(false)
        mAddNewHouseButton.visibility = View.VISIBLE
        setRightTitleText(if (mData.size <= 0) "" else {
            getText(R.string.edit).toString()
        })
        mTitleView?.isSelected = false
        setLeftTitleImage("", R.drawable.ic_back)

        setMiddleTitleText(getString(R.string.my_family))

        mDoneTextView.visibility = View.GONE
    }

    override fun initEditMode() {
        mIsEditMode = true
        mHouseLayoutAdapter.setDeleteMode(true)
        mAddNewHouseButton.visibility = View.GONE
        setRightTitleText(getText(R.string.cancel).toString())

        setLeftTitleImage(getString(R.string.select_all), R.drawable.ic_checkbox)
        setMiddleTitleText(getString(R.string.my_family))

        mDoneTextView.visibility = View.VISIBLE
    }

    override fun onItemClick(position: Int, viewId: Int) {
        val ipsHouse = mData[position]
        val intent = Intent(this, HouseLayoutDetailActivity::class.java)
        intent.putExtra(ExtraConstants.EXTRA_HOUSE_DETAIL, ipsHouse)
        startActivity(intent)
    }


    fun onClickAdd(view: View) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startChoosePictureActivityForResult()
        } else {
            EasyPermissions.requestPermissions(this, "Need permission to choose picture",
                    RequestConstants.REQUEST_CAMERA__STORAGE_PERMISSIONS, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    fun onClickCancel(view: View) {
        mHouseLayoutAdapter.onClickCancel(view)
    }

    fun onClickConfirm(view: View) {
        mHouseLayoutAdapter.onClickConfim(view)
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
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(albumIntent, RequestConstants.REQUEST_ALBUM)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                RequestConstants.REQUEST_ALBUM -> {
                    val bundle = data?.extras
                    data?.data?.let {
                        val filePath = FileUtil.getPathFromUri(it, this@MainActivity)
                        val intent = Intent(this, AddLocatorActivity::class.java)
                        intent.putExtra(ExtraConstants.EXTRA_FILE_PATH, filePath)
                        startActivity(intent)
                    }


                }
            }
        }
    }
}
