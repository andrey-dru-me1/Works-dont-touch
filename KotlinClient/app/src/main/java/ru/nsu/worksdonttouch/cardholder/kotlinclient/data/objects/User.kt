package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    @JsonProperty("login") val login: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("token") val token: String
    )