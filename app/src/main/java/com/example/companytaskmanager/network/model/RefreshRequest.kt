package com.example.companytaskmanager.network.model

data class RefreshRequest(
    val refresh: String,
)

data class RefreshResponse(
    val access: String,
)