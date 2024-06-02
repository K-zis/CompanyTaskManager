package com.example.companytaskmanager.ui.Login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.companytaskmanager.data.repositories.AuthRepository
import com.example.companytaskmanager.utils.InactivityHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

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
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = LoginState.Loading
            try {
                authRepository.loginAction(username, password)
                    .collectLatest { loginResponse ->
                        if (loginResponse.isSuccess) {
                            _loginState.value = LoginState.Success
                            inactivityHandler.resetTimeout()
                        } else {
                            var error: String = ""
                            loginResponse.onFailure {
                                error = it.message.toString().split(" ")[1]
                            }
                            if (error == "400" || error == "401"){
                                _loginState.value = LoginState.Error("Given credentials are wrong. Please try again.")
                            } else {
                                _loginState.value = LoginState.Error("Something went wrong. Make sure you're connected to internet.")
                            }
                        }
                    }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Something went wrong with Server.")
            }
        }
    }

    fun logout() {
        authRepository.logoutAction()
        _loginState.value = LoginState.Idle
        inactivityHandler.stopTimeout()
    }

    fun userInteraction() {
        inactivityHandler.resetTimeout()
        inactivityHandler.resetInactivityFlag()
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}