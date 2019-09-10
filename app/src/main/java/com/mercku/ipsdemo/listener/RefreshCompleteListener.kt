package com.mercku.ipsdemo.listener

interface RefreshCompleteListener {

    // base on different errorCode do diff work,
    // 0 is do nothing(it is success), other wise refresh failure(include null)
    fun onRefreshCompleteListener(errorCode: Int?, isManualRefresh: Boolean = false)
}
