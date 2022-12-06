package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction

import android.util.Log
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.User
import java.io.File

class UserSaveLoad {

    companion object {

        private const val PATH = "/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/files/user.json"

        @JvmStatic
        fun saveUser(user: User) {
            val mapper = ObjectMapper()
            mapper.enable(SerializationFeature.INDENT_OUTPUT)
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)

            val file = File(PATH)
            file.createNewFile()

            mapper.writeValue(file, user)
        }

        @JvmStatic
        fun getUserFromFile() : User? {
            val mapper = ObjectMapper()

            mapper.enable(SerializationFeature.INDENT_OUTPUT)
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            var ret: User? = null
            try {
                ret = mapper.readValue<User>(File(PATH))
            } catch (exception: Throwable) {
                Log.d("INFO", "$exception")
            }

            return ret

        }

    }

}