package com.example.companytaskmanager.network.RRmodels

data class CreateOrUpdateTodoRequest (
    val title: String,
    val content: String,
    val completed: Boolean,
)