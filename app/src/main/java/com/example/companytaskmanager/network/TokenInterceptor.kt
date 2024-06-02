package com.example.companytaskmanager.network

import android.content.SharedPreferences
import android.util.Log
import com.example.companytaskmanager.BuildConfig
import com.example.companytaskmanager.network.RRmodels.RefreshRequest
import com.example.companytaskmanager.network.services.AuthService
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class TokenInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var response = chain.proceed(originalRequest)
        val accessToken = sharedPreferences.getString("access", null)

        if (response.code == 401) {
            synchronized(this) {
                // close previous response in case user is already
                // logged in and need to refresh access key
                if (accessToken != null) {
                    response.close()
                }

                val newAccessToken = refreshAccessToken() ?: return response
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                response = chain.proceed(newRequest)


            }
        }

        // close response if user unauthorized (initial login)
        if (accessToken == null) {
            response.close()
        }
        return response
    }

    private fun refreshAccessToken(): String? {
        val refreshToken = sharedPreferences.getString("refresh", null) ?: return null

        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val authService = retrofit.create(AuthService::class.java)
            val response = authService.refreshToken(RefreshRequest(refreshToken)).execute()

            if (response.isSuccessful) {
                response.body()?.let {
                    saveTokens(it.access)
                    return it.access
                }
            } else {
                Log.e("TokenInterceptor", "Token refresh failed")
            }
            null
        } catch (e: Exception) {
            Log.e("TokenInterceptor", "Token refresh exception: ${e.message}")
            null
        }
    }

    private fun saveTokens(accessToken: String) {
        sharedPreferences.edit().apply {
            putString("access", accessToken)
            apply()
        }
    }
}