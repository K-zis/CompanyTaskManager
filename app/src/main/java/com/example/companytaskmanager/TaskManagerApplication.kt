package com.example.companytaskmanager

import android.app.Application
import com.example.companytaskmanager.utils.SharedPrefsHelper

class TaskManagerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefsHelper.initialize(this)
    }
}