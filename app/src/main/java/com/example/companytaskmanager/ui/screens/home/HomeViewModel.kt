package com.example.companytaskmanager.ui.screens.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.companytaskmanager.network.RetrofitClient
import com.example.companytaskmanager.model.Todo
import com.example.companytaskmanager.network.model.CreateOrUpdateTodoRequest
import com.example.companytaskmanager.utils.SharedPrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _todos = MutableStateFlow<List<Todo>?>(null)
    val todos: StateFlow<List<Todo>?> = _todos

    private val _todoError = MutableStateFlow<String?>(null)
    val todoError: StateFlow<String?> = _todoError

    private val sharedPreferences = SharedPrefsHelper.getSharedPreferences()

    fun fetchTodos() {
        viewModelScope.launch {
            try {
                val access = sharedPreferences.getString("access", null)
                if (!access.isNullOrEmpty()) {
                    val response = RetrofitClient.authService.getTodos("Bearer $access")
                    if (response.isSuccessful) {
                        _todos.value = response.body()
                    } else {
                        _todoError.value = "Failed to fetch todos. Please try again."
                    }
                } else {
                    _todoError.value = "No access token found. Please log in again."
                }
            } catch (e: Exception) {
                _todoError.value = "Failed to fetch todos. Please try again."
            }
        }
    }

    fun createTodo(title: String, content: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                val access = sharedPreferences.getString("access", null)
                if (!access.isNullOrEmpty()) {
                    val response = RetrofitClient.authService.createTodo("Bearer $access", CreateOrUpdateTodoRequest(title, content, completed))
                    if (response.isSuccessful) {
                        fetchTodos() // Refresh todos after creating a new one
                    } else {
                        if (response.code() == 401){
                            Log.d("RESPOSNE", response.message())

                        }
                        _todoError.value = "Failed to create todo. Please try again."
                    }
                } else {
                    _todoError.value = "No access token found. Please log in again."
                }
            } catch (e: Exception) {
                _todoError.value = "Failed to create todo. Please try again."
            }
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                val access = sharedPreferences.getString("access", null)
                if (!access.isNullOrEmpty()) {
                    val response = RetrofitClient.authService.updateTodo(
                        "Bearer $access",
                        CreateOrUpdateTodoRequest(
                            todo.title,
                            todo.content,
                            todo.completed
                        ),
                        todo.id
                    )
                    if (response.isSuccessful) {
                        fetchTodos()
                    } else {
                        _todoError.value = "Failed to update todo. Please try again."
                    }
                } else {
                    _todoError.value = "No access token found. Please log in again."
                }
            } catch (e: Exception) {
                _todoError.value = "Failed to update todo. Please try again."
            }
        }
    }

    fun searchTodos(searchItem: String) {
        viewModelScope.launch {
            try {
                val access = sharedPreferences.getString("access", null)
                if (!access.isNullOrEmpty()) {
                    val response = RetrofitClient.authService.searchTodos(
                        "Bearer $access",
                        searchItem
                    )
                    if (response.isSuccessful) {
                        _todos.value = response.body()
                    } else {
                        _todoError.value = "Failed to search todos. Please try again."
                    }
                } else {
                    _todoError.value = "No access token found. Please log in again."
                }
            } catch (e: Exception) {
                _todoError.value = "Failed to search todos. Please try again."
            }
        }
    }

    fun deleteTodo(todoId: Int) {
        viewModelScope.launch {
            try {
                val access = sharedPreferences.getString("access", null)
                if (!access.isNullOrEmpty()) {
                    val response = RetrofitClient.authService.deleteTodo("Bearer $access", todoId)
                    if (response.isSuccessful) {
                        fetchTodos() // Refresh todos after creating a new one
                    } else {
                        _todoError.value = "Failed to delete todo. Please try again."
                    }
                } else {
                    _todoError.value = "No access token found. Please log in again."
                }
            } catch (e: Exception) {
                _todoError.value = "Failed to delete todo. Please try again."
            }
        }
    }
}