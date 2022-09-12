package com.example.timeisapp.database

import io.ktor.client.*
import io.ktor.client.engine.cio.*

const val BACKEND_URL: String = "http://127.0.0.1:5000"

fun buildClient() {
    val client = HttpClient(CIO)
    //  @TODO:Finire il client e considerare le route per le request
}