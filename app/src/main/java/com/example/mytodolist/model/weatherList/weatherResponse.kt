package com.example.mytodolist.model.weatherList

data class weatherResponse(
    val cod: String,
    val count: Int,
    val list: List<weatherData>,
    val message: String
)