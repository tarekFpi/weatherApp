package com.example.mytodolist.repository

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mytodolist.R
import com.example.mytodolist.retrofit.ApiService
import com.example.mytodolist.view.WeatherActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class UserLocationRepository @Inject constructor(val apiService: ApiService,
                                                 private val context: Context,
)
  {

    private val CHANNEL_ID = "transactions_reminder_channel"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {

            GlobalScope.launch(Dispatchers.IO){

           getUserLocation(location.latitude,location.longitude)

                }

                }

            }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun getUserLocation(lat:Double , lon:Double){

        val  response=  apiService.postUserLocation(lat,lon,50,"e384f9ac095b2109c751d95296f8ea76")

        try {

            if(response.isSuccessful && response.body() != null){

                val value = response.body()!!.main.temp
                createReminderNotification(value)

            } else if(response.errorBody()!=null){
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                Log.d("TAG", "$errorObj")
            }
            else{

                Log.d("TAG", "Something Went Wrong")

            }

        } catch(e: Exception) {

            Log.d("TAG", "${e.message}")

        }

    }

    @SuppressLint("WrongConstant")
    fun createReminderNotification(temp:Double) {

        val intent = Intent(context, WeatherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT)

        createNotificationChannel(context) // This won't create a new channel everytime, safe to call


        //Program to convert Fahrenheit into Celsius

        val temp =((temp.toInt() -32)* 5)/9

        val value = temp.toString().split(".")

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Weather App")
            .setContentText("Current Temparcher: ${value[0]}°C")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // For launching the MainActivity
            .setAutoCancel(true) // Remove notification when tapped

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminders"
            val descriptionText = "This channel sends daily reminders to add your transactions"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}