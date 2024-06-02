package com.example.companytaskmanager.utils

sealed class TodosResourceState<T> {
    class Loading<T> : TodosResourceState<T>()
    data class Success<T>(val data: T) : TodosResourceState<T>()
    data class Error<T>(val error: String) : TodosResourceState<T>()
}

data class TodoState (
    val loading : Boolean = false,
    val success : Boolean = false,
    val errorMessage: String? = null
)