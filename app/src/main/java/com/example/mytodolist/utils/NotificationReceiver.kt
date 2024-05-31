package com.example.mytodolist.utils

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.mytodolist.repository.UserLocationRepository
import com.example.mytodolist.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class NotificationReceiver @AssistedInject constructor(@Assisted val userLocationRepository: UserLocationRepository,
                                                       @Assisted appContext: Context,
                                                       @Assisted params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {


        try {

            userLocationRepository.getLastKnownLocation()

            Log.d("doWork", "Hello from MyWorker!")
            return Result.success()

        }catch (e:Exception){

            return Result.failure()
        }

    }

}