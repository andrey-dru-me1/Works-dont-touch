package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import java.io.*
import java.net.URI
import java.nio.file.Paths

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

    companion object {

        private const val PATH = "/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/files/cards.json"

        @JvmStatic
        fun saveCards(cards: Collection<Card>) {
            val mapper = ObjectMapper()
            mapper.enable(SerializationFeature.INDENT_OUTPUT)
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)

            val file = File(PATH)
            file.createNewFile()

            mapper.writeValue(file, cards)
        }

        @JvmStatic
        fun getCardsFromFile() : Collection<Card>? {
            val mapper = ObjectMapper()
            mapper.enable(SerializationFeature.INDENT_OUTPUT)
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)

            var ret: Collection<Card>? = null
            try {
                ret = mapper.readValue<Collection<Card>>(File(PATH))
            }
            catch (exception: Throwable) {
                Log.d("INFO", "$exception")
            }

            return ret

        }

    }
}