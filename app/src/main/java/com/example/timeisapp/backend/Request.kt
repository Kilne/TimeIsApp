package com.example.timeisapp.database


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


const val BACKEND_URL: String = "http://10.0.2.2:5000"

//const val BACKEND_URL: String = "https://tmiapp.herokuapp.com/"
fun buildClient(): HttpClient {
    return HttpClient(CIO) {
        install(UserAgent) {
            agent = "TimeIsApp Android Client"
        }
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    prettyPrint = true
                }
            )
        }
    }
}

suspend fun isAlive(client: HttpClient): Boolean {

    try {
        client.get("$BACKEND_URL/home")
    } catch (e: Exception) {
        client.close()
        return false
    }
    return true
}

suspend fun logMeIn(username: String, password: String): Pair<Int, String> {

    @kotlinx.serialization.Serializable
    data class ClientAuth(val username: String, val password: String)

    val client = buildClient()
    val isAlive = isAlive(client)
    return if (isAlive){
        val response: HttpResponse = client.post("$BACKEND_URL/mobile/login") {
            contentType(ContentType.Application.Json)
            setBody(ClientAuth(username, password))
        }
        client.close()

        val user = response.body<String>()
        (response.status.value to user)
    } else {
        client.close()
        (500 to "Server is down")
    }


}

suspend fun registerMe(username: String, password: String, email: String): Pair<Int, String> {

    @kotlinx.serialization.Serializable
    data class ClientAuth(val username: String, val password: String, val email: String)

    val client = buildClient()
    val isAlive = isAlive(client)
    return if (isAlive){
        val response: HttpResponse = client.post("$BACKEND_URL/mobile/register") {
            contentType(ContentType.Application.Json)
            setBody(ClientAuth(username, password, email))
        }
        client.close()
        val user = response.body<String>()
        (response.status.value to user)
    }else{
        client.close()
        (500 to "Server is down")
    }


}
