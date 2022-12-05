package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction

import android.os.Environment
import java.io.*
import java.net.URI

class CardsData {

    fun saveImage(sourceuri: URI) {
        val sourceFilename: String = sourceuri.path
        val destinationFilename = "/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient.data/"
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(sourceFilename))
            bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
            val buf = ByteArray(1024)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (bis != null) bis.close()
                if (bos != null) bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}