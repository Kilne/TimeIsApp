package com.example.timeisapp.backend


import com.example.timeisapp.database.Project
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// Constants and global variables
const val BACKEND_URL: String = "http://10.0.2.2:5000"
//const val BACKEND_URL: String = "http://192.168.178.20:5000"

// Client class
class MyClient {
    private var client: HttpClient = HttpClient(CIO) {
        install(UserAgent) {
            agent = "TimeIsApp Android Client"
        }
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                prettyPrint = true
            })
        }
        install(HttpCookies)
    }

    fun getClient(): HttpClient {
        return this.client
    }
}

var client1: HttpClient? = null

suspend fun isAlive(client: HttpClient?): Boolean {

    try {
        client?.get("$BACKEND_URL/mobile/ping_me")
    } catch (e: Exception) {
        return false
    }
    return true
}

suspend fun logMeIn(username: String, password: String, client: HttpClient?): Pair<Int, String> {

    @kotlinx.serialization.Serializable
    data class ClientAuth(val username: String, val password: String)

    return if (isAlive(client)) {
        val response: HttpResponse? = client?.post("$BACKEND_URL/mobile/login") {
            contentType(ContentType.Application.Json)
            setBody(ClientAuth(username, password))
        }
        if (response != null) {
            val user = response.body<String>()
            (response.status.value to user)
        } else {
            (0 to "")
        }
    } else {
        (500 to "Server is down")
    }
}

suspend fun logOut(client: HttpClient?): Boolean {

    return if (isAlive(client)) {
        val response = client?.get("$BACKEND_URL/mobile/logout")
        response!!.status.value == 200
    } else {
        false
    }
}

suspend fun registerMe(
    username: String, password: String, email: String, client: HttpClient?
): Pair<Int, String> {

    @kotlinx.serialization.Serializable
    data class ClientAuth(val username: String, val password: String, val email: String)

    return if (isAlive(client)) {
        val response: HttpResponse = client!!.post("$BACKEND_URL/mobile/register") {
            contentType(ContentType.Application.Json)
            setBody(ClientAuth(username, password, email))
        }
        val user = response.body<String>()
        (response.status.value to user)
    } else {
        (500 to "Server is down")
    }
}

suspend fun deleteProjects(
    projects: MutableList<String>, session: String, client: HttpClient?
): Pair<Boolean, MutableMap<String, Boolean>> {

    @kotlinx.serialization.Serializable
    data class ClientProjects(val project_ids: MutableList<String>, val session_id: String)

    @kotlinx.serialization.Serializable
    data class CompletedResponse(
        val status: Boolean, val completed_dict: MutableMap<String, Boolean> = mutableMapOf()
    )

    return if (isAlive(client)) {
        val response: HttpResponse = client!!.post("$BACKEND_URL/mobile/delete_projects") {
            contentType(ContentType.Application.Json)
            setBody(ClientProjects(projects, session))
        }

        val completed = Json.decodeFromString<CompletedResponse>(response.body())

        if (response.status.value == 200) Pair(true, completed.completed_dict) else Pair(
            false, mutableMapOf()
        )
    } else {
        Pair(false, mutableMapOf())
    }
}

suspend fun getProjects(
    session_id: String,
    client: HttpClient?
): Pair<Boolean, ArrayList<Project>> {

    @kotlinx.serialization.Serializable
    data class ClientSession(val session_id: String)

    @kotlinx.serialization.Serializable
    data class ProjectResponse(val user_projects: ArrayList<Project>)

    val response: HttpResponse = client!!.post("$BACKEND_URL/mobile/get_project") {
        contentType(ContentType.Application.Json)
        setBody(ClientSession(session_id))
    }
    return when (response.status.value) {
        200 -> {
            val project = Json.decodeFromString<ProjectResponse>(response.body())
            Pair(true, project.user_projects)
        }
        else -> {
            Pair(false, arrayListOf())
        }
    }
}

suspend fun addAProject(session_id: String, newProject: Project, client: HttpClient?): Boolean {

    @kotlinx.serialization.Serializable
    data class ClientProject(val session_id: String, val new_project: Project)

    val response: HttpResponse = client!!.post("$BACKEND_URL/mobile/add_a_project") {
        contentType(ContentType.Application.Json)
        setBody(ClientProject(session_id, newProject))
    }
    return response.status.value == 200
}

suspend fun modifyExisting(
    session_id: String,
    modifiedProject: Project,
    client: HttpClient?
): Boolean {

    @kotlinx.serialization.Serializable
    data class ClientProject(val session_id: String, val modified_project: Project)

    @kotlinx.serialization.Serializable
    data class ProjectResponse(val status: Boolean)

    val response: HttpResponse = client!!.post("$BACKEND_URL/mobile/modify_project") {
        contentType(ContentType.Application.Json)
        setBody(ClientProject(session_id, modifiedProject))
    }
    val status = response.body<ProjectResponse>()
    return response.status.value == 200 && status.status
}

suspend fun changeDetailsUser(
    session_id: String,
    username: String = "",
    password: String = "",
    email: String = "",
    client: HttpClient?
): Pair<Boolean, Map<String, Boolean>> {

    @kotlinx.serialization.Serializable
    data class ClientUser(
        val session_id: String,
        val username: String = "",
        val password: String = "",
        val email: String = ""
    )

    @kotlinx.serialization.Serializable
    data class ServerResponse(
        val email: Boolean = false,
        val username: Boolean = false,
        val password: Boolean = false
    )

    val response: HttpResponse = client!!.post("$BACKEND_URL/mobile/modify_user_details") {
        contentType(ContentType.Application.Json)
        setBody(ClientUser(session_id, username, password, email))
    }
    val status = response.body<ServerResponse>()

    return if (response.status.value == 200) {
        var countFalse = 0
        if (!status.email) countFalse++
        if (!status.username) countFalse++
        if (!status.password) countFalse++

        if (countFalse == 3) Pair(
            false,
            mapOf("email" to false, "username" to false, "password" to false)
        )
        else Pair(
            true,
            mapOf(
                "email" to status.email,
                "username" to status.username,
                "password" to status.password
            )
        )
    } else {
        Pair(false, mapOf("email" to false, "username" to false, "password" to false))
    }
}