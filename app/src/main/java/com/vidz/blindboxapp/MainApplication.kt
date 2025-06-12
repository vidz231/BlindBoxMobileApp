package com.vidz.blindboxapp

import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.WorkManager
import com.vidz.base.components.NotificationBannerManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : MultiDexApplication(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Initialize WorkManager with Hilt
        WorkManager.initialize(this, workManagerConfiguration)
        
        // Initialize notification channels for banner notifications
        NotificationBannerManager.initializeNotificationChannels(this)
    }
}