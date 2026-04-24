package com.sedilant.yambol

import android.app.Application
import com.sedilant.yambol.data.di.dataModule
import com.sedilant.yambol.data.di.firestoreModule
import com.sedilant.yambol.di.viewModelModule
import com.sedilant.yambol.domain.di.useCaseModule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class YambolApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        initSentry()
        startKoin {
            androidContext(this@YambolApplication)
            modules(dataModule, firestoreModule, useCaseModule, viewModelModule)
        }
    }

    private fun initSentry() {
        val dsn = BuildConfig.SENTRY_DSN
        if (dsn.isNotBlank()) {
            SentryAndroid.init(this) { options ->
                options.dsn = dsn
                options.isEnableUserInteractionTracing = true
            }
        }
    }
}
