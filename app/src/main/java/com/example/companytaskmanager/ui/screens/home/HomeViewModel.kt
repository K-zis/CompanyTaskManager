package com.example.companytaskmanager.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.companytaskmanager.data.repositories.TodoRepository
import com.example.companytaskmanager.model.Todo
import com.example.companytaskmanager.utils.TodoState
import com.example.companytaskmanager.utils.TodosResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _todosResourceState = MutableStateFlow<TodosResourceState<List<Todo>>>(TodosResourceState.Loading())
    val todosResourceState: StateFlow<TodosResourceState<List<Todo>>> = _todosResourceState

    private val _suggestions = MutableStateFlow<List<Todo>?>(null)
    val suggestions: StateFlow<List<Todo>?> = _suggestions

    private val _todoState = MutableStateFlow<TodoState>(TodoState())
    val todoState: StateFlow<TodoState> = _todoState

    fun fetchTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getTodosAction()
                .collectLatest { response ->
                    _todosResourceState.value = response
                }
        }
    }

    fun searchTodos(searchItem: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.searchTodosAction(searchItem)
                .collectLatest { response ->
                    _todosResourceState.value = response
                }
        }
    }

    fun createTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.createTodoAction(todo            )
                .collectLatest { response ->
                    _todoState.value = response.copy()
                }
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.updateTodoAction(todo)
                .collectLatest { response ->
                    _todoState.value = response.copy()
                }
        }
    }

    fun deleteTodo(todoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteTodoAction(todoId)
                .collectLatest { response ->
                    _todoState.value = response.copy()
                }
        }
    }


    fun fetchSuggestions(query: String) {
        _suggestions.value = todosResourceState.value.let {
            when (it) {
                is TodosResourceState.Success -> it.data.filter { item ->
                    item.title.contains(query, ignoreCase = true) ||
                            item.content.contains(query, ignoreCase = true)
                }

                else -> emptyList()
            }
        }
    }

    fun resetTodoState() {
        _todoState. value = TodoState()
    }
}