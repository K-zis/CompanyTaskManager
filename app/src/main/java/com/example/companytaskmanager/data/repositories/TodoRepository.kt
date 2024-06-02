package com.example.companytaskmanager.data.repositories


import android.content.SharedPreferences
import com.example.companytaskmanager.model.Todo
import com.example.companytaskmanager.network.RRmodels.CreateOrUpdateTodoRequest
import com.example.companytaskmanager.network.services.TodoService
import com.example.companytaskmanager.utils.TodoState
import com.example.companytaskmanager.utils.TodosResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoService: TodoService,
    private val sharedPreferences: SharedPreferences
) {
    suspend fun getTodosAction(): Flow<TodosResourceState<List<Todo>>> = flow {
        emit(TodosResourceState.Loading())
        val response = todoService.getTodos("Bearer ${sharedPreferences.getString("access", null)}")

        if (response.isSuccessful && response.body() != null) {
            emit(TodosResourceState.Success(response.body()!!))
        } else {
            emit(TodosResourceState.Error("Error fetching todos."))
        }
    }.catch { e ->
        emit(TodosResourceState.Error(e.localizedMessage?:"Some error in flow"))
    }

    suspend fun searchTodosAction(query: String): Flow<TodosResourceState<List<Todo>>> = flow {
        emit(TodosResourceState.Loading())
        val response = todoService.searchTodos(
            "Bearer ${sharedPreferences.getString("access", null)}",
            query
        )

        if (response.isSuccessful && response.body() != null) {
            emit(TodosResourceState.Success(response.body()!!))
        } else {
            emit(TodosResourceState.Error("Error searching todos."))
        }
    }.catch { e ->
        emit(TodosResourceState.Error(e.localizedMessage?:"Some error in flow"))
    }

    suspend fun createTodoAction(todo: Todo): Flow<TodoState> = flow {
        emit(TodoState(
                loading = true,
                success = false,
                errorMessage = null
        ))
        val response = todoService.createTodo(
            "Bearer ${sharedPreferences.getString("access", null)}",
            CreateOrUpdateTodoRequest(
                title = todo.title,
                content = todo.content,
                completed = todo.completed
            ))

        if (response.isSuccessful) {
            emit(TodoState(
                    loading = false,
                    success = true,
                    errorMessage = null
            ))
        } else {
            emit(TodoState(
                    loading = false,
                    success = false,
                    errorMessage = "Error fetching todos."
            ))
        }
    }.catch { e ->
        emit(TodoState(
                loading = false,
                success = false,
                errorMessage = "Some error in flow. $e"
        ))
    }

    suspend fun updateTodoAction(todo: Todo): Flow<TodoState> = flow {
        emit(TodoState(
            loading = true,
            success = false,
            errorMessage = null
        ))
        val response = todoService.updateTodo("Bearer ${sharedPreferences.getString("access", null)}", CreateOrUpdateTodoRequest(
            title = todo.title,
            content = todo.content,
            completed = todo.completed
        ), todo.id!!)

        if (response.isSuccessful) {
            emit(TodoState(
                loading = false,
                success = true,
                errorMessage = null
            ))
        } else {
            emit(TodoState(
                loading = false,
                success = false,
                errorMessage = "Error updating todo."
            ))
        }
    }.catch { e ->
        emit(TodoState(
            loading = false,
            success = false,
            errorMessage = "Some error in flow. $e"
        ))
    }


    suspend fun deleteTodoAction(todoId: Int): Flow<TodoState> = flow {
        emit(TodoState(
            loading = true,
            success = false,
            errorMessage = null
        ))
        val response = todoService.deleteTodo("Bearer ${sharedPreferences.getString("access", null)}", todoId)

        if (response.isSuccessful) {
            emit(TodoState(
                loading = false,
                success = true,
                errorMessage = null
            ))
        } else {
            emit(TodoState(
                loading = false,
                success = false,
                errorMessage = "Error deleting todo."
            ))
        }
    }.catch { e ->
        emit(TodoState(
            loading = false,
            success = false,
            errorMessage = "Some error in flow. $e"
        ))
    }
}