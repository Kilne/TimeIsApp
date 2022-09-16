package com.example.timeisapp.database

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


const val BACKEND_URL: String = "http://10.0.2.2:5000"

fun buildClient() : HttpClient {
    return HttpClient(CIO){
        install(UserAgent) {
            agent = "TimeIsApp Android Client"
        }
        install(ContentNegotiation)
    }

}

suspend fun logMeIn(username: String, password: String) {


    val client =  buildClient()
    val response: HttpResponse = client.post("$BACKEND_URL/mobile/login") {
        contentType(ContentType.Application.Json)
        setBody()
        }
    client.close()

    return response.body()


    }
