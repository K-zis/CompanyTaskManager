package com.example.companytaskmanager.network.services

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import com.example.companytaskmanager.network.RRmodels.LoginRequest
import com.example.companytaskmanager.network.RRmodels.LoginResponse
import com.example.companytaskmanager.network.RRmodels.RefreshRequest
import com.example.companytaskmanager.network.RRmodels.RefreshResponse
import retrofit2.Response

interface AuthService {
    @POST("/auth/token/")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/auth/token/refresh/")
    fun refreshToken(
        @Body request: RefreshRequest
    ): Call<RefreshResponse>
}