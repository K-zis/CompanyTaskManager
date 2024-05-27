package com.example.companytaskmanager.model

data class Todo(
    val id: Int,
    val title: String,
    val content: String,
    var completed: Boolean = false
)
