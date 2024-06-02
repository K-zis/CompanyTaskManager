package com.example.companytaskmanager.data.repositories

import android.content.SharedPreferences
import com.example.companytaskmanager.network.services.AuthService
import com.example.companytaskmanager.network.RRmodels.LoginRequest
import com.example.companytaskmanager.network.RRmodels.RefreshRequest
import com.example.companytaskmanager.network.RRmodels.RefreshResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.io.IOException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val sharedPreferences: SharedPreferences
) {

    fun loginAction(username:String, password: String): Flow<Result<Unit>> = flow {
        try {
            val response = authService.login(LoginRequest(username, password)).awaitResponse()
            if (response.isSuccessful) {

                response.body()?.let {
                    sharedPreferences.edit()
                        .putString("access", it.access)
                        .putString("refresh", it.refresh)
                        .apply()
                    emit(Result.success(Unit))
                } ?: emit(Result.failure<Unit>(Exception("No body found")))
            } else {
                emit(Result.failure<Unit>(Exception("Error: ${response.code()}")))
            }
        } catch (e: IOException) {
            emit(Result.failure<Unit>(e))
        } catch (e: HttpException) {
            emit(Result.failure<Unit>(e))
        }
    }

    suspend fun refreshTokenAction(refreshToken: String): Flow<Result<RefreshResponse>> = flow {
        try {
            val response = authService.refreshToken(RefreshRequest(refreshToken)).awaitResponse()
            if (response.isSuccessful) {
                response.body()?.let {
                    sharedPreferences.edit()
                        .putString("access", it.access)
                        .apply()
                    emit(Result.success(it))
                } ?: emit(Result.failure<RefreshResponse>(Exception("No body found")))
            } else {
                emit(Result.failure<RefreshResponse>(Exception("Error: ${response.code()}")))
            }
        } catch (e: IOException) {
            emit(Result.failure<RefreshResponse>(e))
        } catch (e: HttpException) {
            emit(Result.failure<RefreshResponse>(e))
        }
    }

    fun logoutAction() {
        sharedPreferences.edit().clear().apply()

    }
}