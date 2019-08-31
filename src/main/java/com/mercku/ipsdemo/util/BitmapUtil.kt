package com.mercku.ipsdemo.util

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Created by yanqiong.ran on 2019-08-31.
 */
object BitmapUtil {
    public fun resizeBitmap(bitmap: Bitmap, w: Int, h: Int): Bitmap? {
        if (bitmap != null) {
            var width = bitmap.getWidth()
            var height = bitmap.getHeight()
            var newWidth = w
            var newHeight = h
            var scaleWidth = newWidth.toFloat() / width
            var scaleHeight = newHeight.toFloat() / height
            var scale = if (scaleWidth < scaleHeight) {
                scaleWidth
            } else {
                scaleHeight
            }
            var matrix = Matrix()
            matrix.postScale(scale, scale)
            var res = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return res;

        } else {
            return null;
        }
    }

}