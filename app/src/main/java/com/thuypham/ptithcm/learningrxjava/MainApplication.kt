package com.thuypham.ptithcm.learningrxjava

import android.app.Application
import com.thuypham.ptithcm.learningrxjava.di.repositoryModule
import com.thuypham.ptithcm.learningrxjava.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {
    companion object {
        lateinit var instance: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(applicationContext)
            modules(
                repositoryModule,
                viewModelModule,
            )
        }
    }

}