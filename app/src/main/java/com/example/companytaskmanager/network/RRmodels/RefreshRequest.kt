package com.example.companytaskmanager.network.RRmodels

data class RefreshRequest(
    val refresh: String,
)

data class RefreshResponse(
    val access: String,
)