package com.example.mytodolist.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mytodolist.R
import com.example.mytodolist.utils.CheckInternetConnection
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initialPermission()
    }

    override fun onResume() {
        super.onResume()

        if (!checkInternetConnection.isInternetAvailable(this))

         Toast.makeText(applicationContext, "No Internet", Toast.LENGTH_SHORT).show()

    }

    private fun initialPermission() {
        Dexter.withActivity(this@SplashScreenActivity)
            .withPermissions(
                "android.permission.INTERNET",
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.WAKE_LOCK",
                "android.permission.CALL_PHONE"
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    loadContent()
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permenantly, navigate user to app settings
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    fun loadContent(){

        if (checkInternetConnection.isInternetAvailable(this)) {
            Timer().schedule(object : TimerTask() {
                override fun run() {

                    goToHome()
                }
            }, 1500)
            return
        }

    }


    private fun goToHome() {

   val intent= Intent(this@SplashScreenActivity,WeatherActivity::class.java)
   startActivity(intent)
    this.finish()

    }
}