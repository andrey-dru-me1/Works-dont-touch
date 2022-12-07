package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction

import android.util.Log
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import java.io.*

class CardsSaveLoad {

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