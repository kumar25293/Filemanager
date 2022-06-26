package com.demo.samplefileexplorer


import android.app.Application
import com.demo.samplefileexplorer.daggerinjection.component.AppComponent
import com.demo.samplefileexplorer.daggerinjection.component.DaggerAppComponent

class App : Application(){

    companion object{ lateinit var appComponent: AppComponent }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .build()
    }
}
