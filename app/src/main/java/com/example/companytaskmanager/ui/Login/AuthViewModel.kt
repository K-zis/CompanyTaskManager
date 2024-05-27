package com.example.companytaskmanager.ui.Login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.companytaskmanager.network.RetrofitClient
import com.example.companytaskmanager.network.model.LoginRequest
import com.example.companytaskmanager.utils.InactivityHandler
import com.example.companytaskmanager.utils.SharedPrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.awaitResponse
import java.net.SocketTimeoutException

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val sharedPreferences = SharedPrefsHelper.getSharedPreferences()

    private val inactivityHandler = InactivityHandler(5 * 60 * 1000L)

    init {
        monitorInactivity()

    }

    private fun monitorInactivity() {
        viewModelScope.launch {
            inactivityHandler.inactivityFlow.collectLatest { isInactive ->
                if (isInactive) {
                    logout()
                }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = withTimeoutOrNull(5000) { // Set a timeout of 5 seconds
                    RetrofitClient.authService.login(LoginRequest(username, password)).awaitResponse()
                }

                if (response == null) {
                    _loginError.value = "Login timeout. Please check your connection and try again."
                } else if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        sharedPreferences.edit()
                            .putString("access", loginResponse.access)
                            .putString("refresh", loginResponse.refresh)
                            .apply()

                        _isAuthenticated.value = true
                        _loginError.value = null
                        inactivityHandler.resetTimeout()
                    } else {
                        Log.d("LOGGING RESPONSE NULL", loginResponse.toString())
                        _loginError.value = "Login failed. Please try again."
                    }
                } else {
                    _loginError.value = "Login failed. Invalid credentials."
                }
            } catch (e: SocketTimeoutException) {
                _loginError.value = "Login timeout. Please check your connection and try again."
            } catch (e: Exception) {
                Log.d("EXCEPTION THROWN", e.toString())
                _loginError.value = "Login failed. Please try again."
            }
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
        _isAuthenticated.value = false
        inactivityHandler.stopTimeout()
    }

    fun userInteraction() {
        inactivityHandler.resetTimeout()
        inactivityHandler.resetInactivityFlag()
    }
}