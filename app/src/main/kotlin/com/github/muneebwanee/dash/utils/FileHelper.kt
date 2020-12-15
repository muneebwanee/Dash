package com.github.muneebwanee.dash.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.utils.Consts.ADDRESS_AUDIO_CALLS
import com.github.muneebwanee.dash.utils.Consts.ADDRESS_AUDIO_RECORD
import com.github.muneebwanee.dash.utils.Consts.ADDRESS_IMAGE
import com.github.muneebwanee.dash.utils.Consts.TAG
import com.pawegio.kandroid.e
import com.pawegio.kandroid.longToast
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

/**
 * Created by muneebwanee on 15/12/20.
 */
object FileHelper{
    fun Context.getFilePath(): String =
            if (externalCacheDir != null) externalCacheDir!!.absolutePath
            else cacheDir.absolutePath

    @Throws(Exception::class)
    fun Context.getFileNameCall(number: String?, dateTime:String?): String {
        val file: File?
        var phoneNumber: String? = number ?: throw Exception("Phone number can't be empty")

        try {
            file = File(getFilePath(), ADDRESS_AUDIO_CALLS)

            if (!file.exists()) {
                file.mkdirs()
            }

            phoneNumber = phoneNumber!!.replace("[*+-]".toRegex(), "")
            if (phoneNumber.length > 10) {
                phoneNumber.substring(phoneNumber.length - 10, phoneNumber.length)
            }
        } catch (e: Exception) {
            throw Exception(e)
        }

        return file.absolutePath + "/" + dateTime + "," + phoneNumber + ".mp3"
    }

    @Throws(Exception::class)
    fun Context.getFileNameAudio(name: String?, dateTime:String?): String {
        val file: File?
        try {
            file = File(getFilePath(), ADDRESS_AUDIO_RECORD)

            if (!file.exists()) {
                file.mkdirs()
            }

        } catch (e: Exception) {
            throw Exception(e)
        }

        return file.absolutePath + "/" + dateTime + "," + name + ".mp3"
    }

    fun Context.deleteFileName(fileName: String?) {
        if (fileName == null)
            return
        try {
            val file = File(fileName)

            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            longToast("${getString(R.string.failed_delete_file)} ${e.message.toString()}")
        }
    }

    fun deleteFile(fileName: String?) {
        if (fileName == null)
            return
        try {
            val file = File(fileName)

            if (file.exists()) {
                file.delete()
            }
        } catch (ex: Exception) {
            e(TAG, ex.message.toString())
        }
    }

    fun Context.deleteAllFile(address:String) {
        try {
            val file = File(getFilePath(), address)
            if (file.isDirectory) {
                for (child in file.listFiles()) child.delete()
            } else {
                file.delete()
            }
        }catch (e:Exception){
            longToast("${getString(R.string.failed_delete_files)} ${e.message.toString()}")
        }
    }

    @Throws(Exception::class)
    fun Context.getContactName(phoneNum: String?): String {
        if (phoneNum==null) throw Exception("Phone number can't be empty")

        var res = phoneNum.replace("[*+-]".toRegex(), "")
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)

        val names = contentResolver.query(uri, projection, null, null, null)
        if (names!=null){
            val indexName = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val indexNumber = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (names.count > 0) {
                names.moveToFirst()
                do {
                    val name = names.getString(indexName)
                    val number = names.getString(indexNumber).replace("[*+-]".toRegex(), "")

                    if (number.compareTo(res) == 0) {
                        res = name
                        break
                    }
                } while (names.moveToNext())
            }

            names.close()
        }

        return res
    }

    @SuppressLint("Recycle")
    fun Uri.getUriPath(context: Context): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(this, projection, null, null, null) ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(columnIndex)
        cursor.close()
        return s
    }

    fun getDurationFile(fileName:String) : String{
        val metaRetriever = MediaMetadataRetriever()
        metaRetriever.setDataSource(fileName)
        val duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
        val seconds = (duration % 60000 / 1000).toString()
        val minutes = (duration / 60000).toString()
        metaRetriever.release()
        return if (seconds.length == 1) "$minutes:0$seconds" else "$minutes:$seconds"
    }

    fun Bitmap.getFileNameBitmap(context: Context,nameImage:String) : String{
        val file = File(context.getFilePath(), ADDRESS_IMAGE)
        if (!file.exists()) file.mkdirs()

        val filePath = file.absolutePath + "/" + nameImage +  ".png"

        val bytes = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fos = FileOutputStream(File(filePath))
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return filePath
    }
}