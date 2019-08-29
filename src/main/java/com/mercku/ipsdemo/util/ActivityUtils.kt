package com.mercku.ipsdemo.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.Toast


/**
 * Activity工具类
 *
 * @author jiali
 */
object ActivityUtils {

    /**
     * Android 8.0非全屏透明Activity不能设置固定方向，因为部分选择框基于Activity实现，需要实现兼容
     *
     * @param activity
     * @param orientation
     */
    fun setOrientation(activity: Activity?, orientation: Int) {
        if (activity == null) {
            return
        }
        val backgroundDrawable = activity.window.decorView.background ?: return

        if (backgroundDrawable is ColorDrawable && Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            val isTransparent = backgroundDrawable.color == Color.TRANSPARENT
                    || backgroundDrawable.color != android.R.color.transparent
            if (isTransparent) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                return
            }
        }
        activity.requestedOrientation = orientation
    }

    fun dp2px(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun sp2px(context: Context, sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun px2dp(context: Context, pxVal: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxVal / scale).toInt()
    }

    fun px2sp(context: Context, pxVal: Float): Int {
        return (pxVal / context.resources.displayMetrics.scaledDensity).toInt()
    }

    fun showKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyBoardDelay(editText: EditText) {
        editText.postDelayed({
            val manager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(editText, 0)
        }, 500)
    }

    fun openWebPage(context: Context?, url: String) {
        if (context == null) {
            return
        }
        try {
            if (!URLUtil.isValidUrl(url)) {
                Toast.makeText(context, "This is not a valid link", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "You don't have any browser to open web page", Toast.LENGTH_LONG).show()
        }
    }

    fun imageTranslucent(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}
