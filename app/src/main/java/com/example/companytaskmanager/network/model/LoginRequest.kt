package com.example.companytaskmanager.network.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val access: String,
    val refresh: String
)


