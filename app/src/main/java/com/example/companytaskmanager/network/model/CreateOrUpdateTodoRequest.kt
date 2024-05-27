package com.example.companytaskmanager.network.model

data class CreateOrUpdateTodoRequest (
    val title: String,
    val content: String,
    val completed: Boolean,
)