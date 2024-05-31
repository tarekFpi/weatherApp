package com.example.mytodolist.repository
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mytodolist.model.weatherList.weatherData
import com.example.mytodolist.retrofit.ApiService
import com.example.mytodolist.utils.Resource

import org.json.JSONObject
import javax.inject.Inject


class WeatherRepository @Inject constructor(val apiService: ApiService,
                                            private val context: Context,

) {

    private var _weatherlistResponseMutableLiveData = MutableLiveData<Resource<List<weatherData>>>()
    val  weatherResponseLiveData : LiveData<Resource<List<weatherData>>>
        get() =_weatherlistResponseMutableLiveData

    suspend  fun getWeatherList(){


        val  response=  apiService.getPost()

        try {
            _weatherlistResponseMutableLiveData.postValue(Resource.Loading())

            if(response.isSuccessful && response.body() != null){

                _weatherlistResponseMutableLiveData.postValue(Resource.Success(response.body()!!.list))

            } else if(response.errorBody()!=null){
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _weatherlistResponseMutableLiveData.postValue(Resource.Error(errorObj.toString()))
            }
            else{
                _weatherlistResponseMutableLiveData.postValue(Resource.Error("Something Went Wrong"))
            }

        } catch(e: Exception) {

            _weatherlistResponseMutableLiveData.postValue(Resource.Error(e.message))
        }


    }



}