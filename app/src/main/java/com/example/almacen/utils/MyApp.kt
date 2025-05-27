package com.example.almacen.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MyApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var userPreferences: UserPreferences
        lateinit var appScope: CoroutineScope
        const val API_SERVER = "http://10.0.2.2"
        const val API_PORT = "8063"

    }
    override fun onCreate(){
        super.onCreate()
        context = this
        userPreferences = UserPreferences()
        appScope = CoroutineScope(Dispatchers.IO)
    }

}