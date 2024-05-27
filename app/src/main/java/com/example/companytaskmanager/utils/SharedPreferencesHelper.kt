package com.example.companytaskmanager.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SharedPrefsHelper {

    private const val PREFS_FILENAME = "auth_prefs"
    private lateinit var sharedPreferences: EncryptedSharedPreferences

    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFS_FILENAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ) as EncryptedSharedPreferences
        }
    }

    fun getSharedPreferences(): EncryptedSharedPreferences {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("SharedPrefsHelper is not initialized. Call initialize() before using this method.")
        }
        return sharedPreferences
    }
}