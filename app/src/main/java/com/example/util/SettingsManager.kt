package com.example.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "pos_settings")

class SettingsManager(private val context: Context) {

    companion object {
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val EMPLOYEE_NAME = stringPreferencesKey("employee_name")
        private val EMPLOYEE_PIN = stringPreferencesKey("employee_pin")
        private val APP_LANGUAGE = stringPreferencesKey("app_language")
        private val REMEMBER_LOGIN = booleanPreferencesKey("remember_login")
        private val PRINTER_NAME_ADDRESS = stringPreferencesKey("printer_name_address")
    }

    val serverUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: "http://192.168.1.100:54345"
    }

    suspend fun setServerUrl(url: String) {
        val normalizedUrl = if (url.endsWith("/")) url.removeSuffix("/") else url
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL] = normalizedUrl
        }
    }

    val employeeName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EMPLOYEE_NAME] ?: "الكاشير 1"
    }

    suspend fun setEmployeeName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[EMPLOYEE_NAME] = name
        }
    }

    val employeePin: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EMPLOYEE_PIN] ?: "1234"
    }

    suspend fun setEmployeePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[EMPLOYEE_PIN] = pin
        }
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[APP_LANGUAGE] ?: "ar"
    }

    suspend fun setAppLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[APP_LANGUAGE] = lang
        }
    }

    val rememberLogin: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_LOGIN] ?: false
    }

    suspend fun setRememberLogin(remember: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_LOGIN] = remember
        }
    }

    val printerNameAddress: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PRINTER_NAME_ADDRESS] ?: ""
    }

    suspend fun setPrinterNameAddress(address: String) {
        context.dataStore.edit { preferences ->
            preferences[PRINTER_NAME_ADDRESS] = address
        }
    }
}
