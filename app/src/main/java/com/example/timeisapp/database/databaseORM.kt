package com.example.timeisapp.database

import java.time.LocalDateTime
import java.util.Date

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