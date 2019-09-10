package com.mercku.ipsdemo.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by yanqiong.ran on 2019-08-28.
 */
/**
 * 图片文件操作
 */
object FileUtil {

    /**
     * 将Bitmap 图片保存到本地路径，并返回路径
     *
     * @param fileName 文件名称
     * @param bitmap   图片
     * @param资源类型，参照 MultimediaContentType 枚举，根据此类型，保存时可自动归类
     */
    fun saveFile(context: Context, fileName: String, bitmap: Bitmap): String {
        return saveFile(context, "", fileName, bitmap)
    }

    fun saveFile(context: Context, filePath: String, fileName: String, bitmap: Bitmap): String {
        val bytes = bitmapToBytes(bitmap)
        return saveFile(context, filePath, fileName, bytes)
    }

    /**
     * Bitmap 转 字节数组
     *
     * @param bm
     * @return
     */
    fun bitmapToBytes(bm: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    fun saveFile(context: Context, file: String?, fileName: String, bytes: ByteArray): String {
        var filePath = file
        var fileFullName = ""
        var fos: FileOutputStream? = null
        val dateFolder = SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
                .format(Date())
        try {
            val suffix = ""
            if (filePath == null || filePath!!.isEmpty()) {
                filePath = Environment.getExternalStorageDirectory().path + "/ips/" + dateFolder + "/"
            }

            val file = File(filePath)
            if (!file.exists()) {
                file.mkdirs()
            }
            val fullFile = File(filePath, fileName + suffix)
            fileFullName = fullFile.getPath()
            fos = FileOutputStream(File(filePath, fileName + suffix))
            fos!!.write(bytes)
        } catch (e: Exception) {
            fileFullName = ""
        } finally {
            if (fos != null) {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                    fileFullName = ""
                }

            }
        }
        return fileFullName
    }

    fun getImageStreamFromExternal(imageName: String): Uri? {
        val externalPubPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        )

        val picPath = File(externalPubPath, imageName)
        var uri: Uri? = null
        if (picPath.exists()) {
            uri = Uri.fromFile(picPath)
        }

        return uri
    }

    fun getPathFromUri(uri: Uri, context: Context): String? {
        var imagePath: String? = null

        if (DocumentsContract.isDocumentUri(context, uri)) {
            var docId: String = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                var id = docId.split(":")[1];
                var selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection!!, context)
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                var contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        docId.toLong());
                imagePath = getImagePath(contentUri, null, context);
            }
        } else if ("content".equals(uri.getScheme(), true)) {
            //Log.d(TAG, "content: " + uri.toString());
            imagePath = getImagePath(uri, null, context);
        }
        return imagePath
    }

    private fun getImagePath(uri: Uri, selection: String?, context: Context): String? {
        var path: String? = null
        val cursor = context.getContentResolver().query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Images.Media.DATA))
            }

            cursor!!.close()
        }
        return path
    }
}