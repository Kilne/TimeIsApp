package com.example.timeisapp.backend

import com.example.timeisapp.database.BACKEND_URL
import com.example.timeisapp.database.Database
import com.example.timeisapp.database.buildClient
import com.example.timeisapp.database.isAlive
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun checkLogin(userdetails: Database): Boolean {
    val client = buildClient()
    return if (isAlive(client)){
        val response: HttpResponse = client.post("$BACKEND_URL/mobile/check_login") {
            contentType(ContentType.Application.Json)
            setBody(userdetails.session_id)
        }
        client.close()

        response.status.value == 200
    }else{
        client.close()
        false
    }
}