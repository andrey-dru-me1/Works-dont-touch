package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction

import android.content.Context
import android.net.Uri
import java.io.*

class ImageSaver {

    companion object {

        fun saveToFile(context: Context, uri: Uri, file: File) {
            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                bis = BufferedInputStream(context.contentResolver.openInputStream(uri))
                bos = BufferedOutputStream(FileOutputStream(file, false))
                val buffer = ByteArray(1024)
                bis.read(buffer)
                do {
                    bos.write(buffer)
                } while (bis.read(buffer) != -1)
            } catch (_: IOException) {
            } finally {
                try {
                    bis?.close()
                    bos?.close()
                } catch (_: IOException) {
                }
            }
        }
    }
}