package com.example.timeisapp.database

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*


const val BACKEND_URL: String = "http://10.0.2.2:5000"

fun buildClient() : HttpClient {
    return HttpClient(CIO){
        install(UserAgent) {
            agent = "TimeIsApp Android Client"
        }
        install(ContentNegotiation){
            json(
                Json {
                    isLenient = true
                    prettyPrint = true
                }

            )
        }
    }

}

suspend fun logMeIn(username: String, password: String): Any {

    val valueMap = mapOf("username" to username, "password" to password)

    val jsonString = Json.encodeToString(valueMap)

    val client =  buildClient()
    val response: HttpResponse = client.post("$BACKEND_URL/mobile/login") {
        contentType(ContentType.Application.Json)
        setBody(jsonString)
        }
    client.close()
    val json : Map<String, JsonElement> = Json.parseToJsonElement(response.body()).jsonObject
    // TODO(Continuare la de serializazzione)
    Log.d("Response", json["projects"]?.jsonObject.toString())
    return if (response.status == HttpStatusCode.OK) {
        Json.parseToJsonElement(response.body()).jsonObject
    } else {
        response.status.value
    }

}
