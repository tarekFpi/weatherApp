package com.example.mytodolist.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.mytodolist.model.getUserLocation.userLocationResponse
import com.example.mytodolist.repository.UserLocationRepository
import com.example.mytodolist.repository.WeatherRepository
import com.example.mytodolist.utils.NotificationReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApplicationComponent : Application(), Configuration.Provider {

    @Inject
    lateinit var locationReceiverFactory: LocationReceiverFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(locationReceiverFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
    }

}

class LocationReceiverFactory @Inject constructor(private val userLocationRepository: UserLocationRepository) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = NotificationReceiver(userLocationRepository, appContext, workerParameters)

}

