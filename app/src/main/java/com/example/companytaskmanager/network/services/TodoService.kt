package com.example.companytaskmanager.network.services

import com.example.companytaskmanager.model.Todo
import com.example.companytaskmanager.network.RRmodels.CreateOrUpdateTodoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoService {
    @GET("/todos/")
    suspend fun getTodos(
        @Header("Authorization") token: String
    ): Response<List<Todo>>

    @POST("/todos/")
    suspend fun createTodo(
        @Header("Authorization") token: String,
        @Body request: CreateOrUpdateTodoRequest
    ): Response<Unit>

    @GET("/todos/")
    suspend fun searchTodos(
        @Header("Authorization") token: String,
        @Query("search") searchItem: String
    ): Response<List<Todo>>

    @PUT("/todos/update/{id}/")
    suspend fun updateTodo(
        @Header("Authorization") token: String,
        @Body request: CreateOrUpdateTodoRequest,
        @Path("id") id: Int
    ): Response<Unit>

    @DELETE("/todos/delete/{id}/")
    suspend fun deleteTodo(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}