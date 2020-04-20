package com.simplepos

import android.app.Application
import com.simplepos.di.posModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(posModule)
        }

    }
}