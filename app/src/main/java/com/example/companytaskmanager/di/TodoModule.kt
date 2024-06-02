package com.example.companytaskmanager.di

import android.content.SharedPreferences
import com.example.companytaskmanager.data.repositories.TodoRepository
import com.example.companytaskmanager.network.services.TodoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodoModule {

    @Provides
    @Singleton
    fun provideTodoService(retrofit: Retrofit): TodoService {
        return retrofit.create(TodoService::class.java)
    }

    @Singleton
    @Provides
    fun provideTodoRepository(todoService: TodoService, sharedPreferences: SharedPreferences): TodoRepository {
        return TodoRepository(todoService, sharedPreferences)
    }
}