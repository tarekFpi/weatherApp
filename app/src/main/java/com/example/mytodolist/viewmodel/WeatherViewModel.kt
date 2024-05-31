package com.example.mytodolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.model.getUserLocation.userLocationResponse
import com.example.mytodolist.model.weatherList.weatherData
import com.example.mytodolist.repository.WeatherRepository
import com.example.mytodolist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository
) : ViewModel() {


    val weatherListResponseLiveData : LiveData<Resource<List<weatherData>>>
        get() = weatherRepository.weatherResponseLiveData

    init {
        viewModelScope.launch(Dispatchers.Main){

            weatherRepository.getWeatherList()
        }
    }


}