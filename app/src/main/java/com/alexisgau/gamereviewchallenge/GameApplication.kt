package com.alexisgau.gamereviewchallenge

import android.app.Application
import com.alexisgau.gamereviewchallenge.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GameApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GameApplication)
            modules(appModule)
        }
    }
}