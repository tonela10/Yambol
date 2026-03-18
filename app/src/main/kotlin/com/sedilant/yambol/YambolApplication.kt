package com.sedilant.yambol

import android.app.Application
import com.sedilant.yambol.data.AppContainer
import com.sedilant.yambol.data.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YambolApplication : Application() {
    /** AppContainer instance used by the rest of the classes to obtain dependencies*/
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
