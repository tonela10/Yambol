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
        SentryAndroid.init(this) { options ->
            options.dsn = "SENTRY_DSN_PLACEHOLDER"
            options.isEnableUserInteractionTracing = true
        }
        startKoin {
            androidContext(this@YambolApplication)
            modules(dataModule, firestoreModule, useCaseModule, viewModelModule)
        }
    }
}
