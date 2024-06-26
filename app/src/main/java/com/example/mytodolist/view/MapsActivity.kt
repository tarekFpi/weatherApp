package com.example.mytodolist.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mytodolist.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Timer
import java.util.TimerTask

class MapsActivity : AppCompatActivity(),  OnMapReadyCallback

     {

    private var mMap: GoogleMap? = null
    val REQUEST_LOCATION_CODE = 99

    private lateinit var  progressBar: ProgressDialog
    private var latitude:Double = 0.0

    private var longitude:Double = 0.0

    private lateinit var textViewTeam:TextView

    private lateinit var textViewCuntryName:TextView

    private lateinit var textViewfeels_like:TextView

    private lateinit var textViewTemp_min:TextView

    private lateinit var textViewTemp_max:TextView

    private lateinit var textViewPressure:TextView

    private lateinit var textViewHumidity:TextView

    private lateinit var textViewDescription:TextView

    private lateinit var countryName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        progressBar = ProgressDialog(this)
        progressBar.setMessage("please wait...")
        progressBar.show()

        textViewCuntryName= findViewById(R.id.textwetherDetails_name)
        textViewfeels_like = findViewById(R.id.feels_like)
        textViewTemp_min = findViewById(R.id.temp_min)
        textViewTemp_max = findViewById(R.id.temp_max)
        textViewPressure = findViewById(R.id.pressure)
        textViewHumidity = findViewById(R.id.humidity)
        textViewTeam = findViewById(R.id.text_Details_degvalue)
        textViewDescription = findViewById(R.id.description)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val bundle: Bundle? = intent.extras

        if (bundle != null) {

            latitude= bundle.getString("lat")!!.toDouble()

            longitude= bundle.getString("lon")!!.toDouble()

            countryName =  bundle.getString("name").toString()

            //Program to convert Fahrenheit into Celsius
            val temp =((bundle.getDouble("temp")-32)* 5)/9

            val value =temp.toString().split(".")

            textViewTeam.text = value[0].toString()
            textViewCuntryName.text = bundle.getString("name")
            textViewHumidity.text ="humidity: "+ bundle.getString("humidity")
            textViewPressure.text ="pressure: "+ bundle.getString("pressure")
            textViewTemp_max.text ="temp max: "+ bundle.getString("temp_max")
            textViewTemp_min.text ="temp min: " +bundle.getString("temp_min")
            textViewfeels_like.text ="feels like: "+ bundle.getString("feels_like")
            textViewDescription.text =bundle.getString("description")


        }
    }

override fun onMapReady(googleMap: GoogleMap) {
   mMap = googleMap
   mMap!!.uiSettings.isZoomControlsEnabled = true
   mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL

   mMap?.uiSettings?.isMyLocationButtonEnabled = true
   mMap?.isTrafficEnabled = true
   mMap?.uiSettings?.isCompassEnabled = true

   if (ContextCompat.checkSelfPermission(
           this,
           Manifest.permission.ACCESS_FINE_LOCATION
       ) == PackageManager.PERMISSION_GRANTED
   ) {

       mMap!!.isMyLocationEnabled = true
       mMap!!.setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener {

           Handler().postDelayed({

               mMap!!.clear()
               getWeatherLocation()

           }, 1500)

           false
       })


       getWeatherLocation()
   }
}



fun checkLocationPermission(): Boolean {
   return if (ContextCompat.checkSelfPermission(
           this,
           Manifest.permission.ACCESS_FINE_LOCATION
       ) != PackageManager.PERMISSION_GRANTED
   ) {
       if (ActivityCompat.shouldShowRequestPermissionRationale(
               this,
               Manifest.permission.ACCESS_FINE_LOCATION
           )
       ) {
           ActivityCompat.requestPermissions(
               this,
               arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
               REQUEST_LOCATION_CODE
           )
       } else {
           ActivityCompat.requestPermissions(
               this,
               arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
               REQUEST_LOCATION_CODE
           )
       }
       false

   } else true
}


  private  fun getWeatherLocation(){

          val latLng = LatLng(latitude, longitude)
          val markerOptions = MarkerOptions()
          markerOptions.position(latLng)
          markerOptions.title("$countryName")
          markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
          mMap?.addMarker(markerOptions)
          mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
          mMap?.animateCamera(CameraUpdateFactory.zoomBy(18f))
          progressBar.dismiss()


    }



}