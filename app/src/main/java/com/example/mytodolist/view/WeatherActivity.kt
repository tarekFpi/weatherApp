package com.example.mytodolist.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.lottie.LottieAnimationView
import com.example.mytodolist.R
import com.example.mytodolist.adapter.WeatherListAdapter
import com.example.mytodolist.model.weatherList.weatherData
import com.example.mytodolist.utils.CheckInternetConnection
import com.example.mytodolist.utils.NotificationReceiver
import com.example.mytodolist.utils.Resource
import com.example.mytodolist.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {

    private lateinit var weatherListAdapter : WeatherListAdapter;

    private lateinit var recyclerView_task: RecyclerView

    private lateinit var  progressDialog: ProgressDialog

    private lateinit var weather_ViewModel: WeatherViewModel

    private var weatherList: ArrayList<weatherData> = ArrayList()

    private lateinit var editText_weatherSearch: EditText

    private lateinit var  lottieAnimationView: LottieAnimationView

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")

        recyclerView_task = findViewById<RecyclerView>(R.id.recyclerView_task)

        editText_weatherSearch= findViewById(R.id.edit_taskSearch)

        lottieAnimationView= findViewById(R.id.animation_view)

        weather_ViewModel= ViewModelProvider(this)[WeatherViewModel::class.java]

        editText_weatherSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable) {

                searchWeatherList(editable.toString())
            }
        })

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()


        weatherListAdapter= WeatherListAdapter(this,weatherList)

        recyclerView_task.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@WeatherActivity)
            adapter=weatherListAdapter

        }

        weather_ViewModel.weatherListResponseLiveData.observe(this@WeatherActivity, Observer {

            when(it){
                is Resource.Loading -> {
                    progressDialog.show()

                }

                is Resource.Error -> {

                    apiError(it.message.toString())
                    progressDialog.dismiss()
                }

                is  Resource.Success -> {

                    if(it.data!!.isEmpty()){

                        lottieAnimationView.visibility= View.VISIBLE
                        progressDialog.dismiss()


                    }else{

                        it.data?.let { it1 ->

                            lottieAnimationView.visibility= View.GONE
                            weatherList.clear()
                            weatherList.addAll(it1.toList())
                            weatherListAdapter.setTaskList(weatherList)
                            weatherListAdapter.notifyDataSetChanged()

                            weatherListAdapter.setOnItemClick(object :WeatherListAdapter.onItemClickLisiner{
                                override fun OnClickLisiner(position: Int) {


                                    startActivity(Intent(this@WeatherActivity, MapsActivity::class.java).apply {
                                         putExtra("lat", it1[position].coord.lat.toString())
                                         putExtra("lon", it1[position].coord.lon.toString())
                                         putExtra("name", it1[position].name.toString())
                                         putExtra("humidity", it1[position].main.humidity.toString())
                                         putExtra("pressure", it1[position].main.pressure.toString())
                                         putExtra("temp_max", it1[position].main.temp_max.toString())
                                         putExtra("temp_min", it1[position].main.temp_min.toString())
                                         putExtra("feels_like", it1[position].main.feels_like.toString())
                                         putExtra("temp", it1[position].main.temp.toString())

                                        for (i in it1[position].weather.toList()) {
                                            putExtra("description", i.description.toString())
                                        }
                                    })

                                }
                            })
                            progressDialog.dismiss()


                        }


                    }
                }
            }
        })


        internetConnection()

        scheduleTaskNotification()
    }



    private fun scheduleTaskNotification(){


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val myWorkRequest =
            OneTimeWorkRequest.Builder(NotificationReceiver::class.java)
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)

    }

    private  fun internetConnection(){

        if (!checkInternetConnection.isInternetAvailable(this))

            Toast.makeText(applicationContext, "No Internet", Toast.LENGTH_SHORT).show()

    }



    private fun apiError(it:String){
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

    private fun searchWeatherList(textData: String) {
        try {
            val list =ArrayList<weatherData>()


            for (item: weatherData in weatherList) {

               if (item.name.toString().lowercase().contains(textData.toString().lowercase()))
                  {
                      list.add(item)
                  }
            }

            weatherListAdapter.filterdList(list)

        } catch (exception: Exception) {

            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

}