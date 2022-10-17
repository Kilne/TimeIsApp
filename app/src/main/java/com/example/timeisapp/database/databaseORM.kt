package com.example.timeisapp.database
import kotlinx.serialization.*
import java.time.LocalDateTime

data class DatabaseORM(
    val username: String,
    val projectORM: Array<ProjectORM>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseORM

        if (username != other.username) return false
        if (!projectORM.contentEquals(other.projectORM)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + projectORM.contentHashCode()
        return result
    }

    fun getUserMap(): Map<String, Any> {
        val userMap = mutableMapOf<String, Any>()
        userMap["username"] = username
        userMap["projectORM"] = projectORM
        return userMap
    }

}

data class ProjectORM(
    val p_name: String, val obj: String, val goal: Float,
    val num_steps_total: Int, val num_steps_completed: Int,
    val single_step_value: Float, val due_date: LocalDateTime, val time_left: Int, val perc_compl: Float,
    val description: String, val p_id: String
) {

    fun getProjectMap(): Map<String, Any> {
        return mutableMapOf<String, Any>(
            "p_name" to p_name,
            "obj" to obj,
            "goal" to goal,
            "num_steps_total" to num_steps_total,
            "num_steps_completed" to num_steps_completed,
            "single_step_value" to single_step_value,
            "due_date" to due_date,
            "time_left" to time_left,
            "perc_compl" to perc_compl,
            "description" to description,
            "p_id" to p_id
        )
    }
}

@Serializable
data class Database(
    val id: String,
    val username: String,
    val session_id: String,
    val user_projects: ArrayList<Project>
): java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Database

        if (id != other.id) return false
        if (username != other.username) return false
        if (session_id != other.session_id) return false
        if (user_projects != other.user_projects) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + session_id.hashCode()
        result = 31 * result + user_projects.hashCode()
        return result
    }
}

@Serializable
data class Project(
    val p_name: String, val obj: String, val goal: Float,
    val num_steps_total: Int, val num_steps_completed: Int,
    val single_step_value: Float, val due_date: String, val time_left: Int, val perc_compl: Float,
    val description: String, val p_id: String
) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        if (p_name != other.p_name) return false
        if (obj != other.obj) return false
        if (goal != other.goal) return false
        if (num_steps_total != other.num_steps_total) return false
        if (num_steps_completed != other.num_steps_completed) return false
        if (single_step_value != other.single_step_value) return false
        if (due_date != other.due_date) return false
        if (time_left != other.time_left) return false
        if (perc_compl != other.perc_compl) return false
        if (description != other.description) return false
        if (p_id != other.p_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = p_name.hashCode()
        result = 31 * result + obj.hashCode()
        result = 31 * result + goal.hashCode()
        result = 31 * result + num_steps_total
        result = 31 * result + num_steps_completed
        result = 31 * result + single_step_value.hashCode()
        result = 31 * result + due_date.hashCode()
        result = 31 * result + time_left
        result = 31 * result + perc_compl.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + p_id.hashCode()
        return result
    }
}
