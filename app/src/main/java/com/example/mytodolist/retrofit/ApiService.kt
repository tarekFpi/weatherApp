package com.example.mytodolist.retrofit
import com.example.mytodolist.model.getUserLocation.userLocationResponse
import com.example.mytodolist.model.weatherList.weatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("/data/2.5/find?lat=23.68&lon=90.35&cnt=50&appid=e384f9ac095b2109c751d95296f8ea76")
    suspend fun getPost(): Response<weatherResponse>

    @GET("/data/2.5/weather")
    suspend fun postUserLocation(@Query("lat") lat:Double,
                                 @Query("lon") lon:Double,
                                 @Query("cnt") cnt:Int,
                                 @Query("appid") appid:String,
                                 ): Response<userLocationResponse>

}