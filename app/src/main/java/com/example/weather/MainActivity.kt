package com.example.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import org.json.JSONException

import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.util.Log
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //get the textbox
        val textView = findViewById<TextView>(R.id.textView)
        var Longitude = 0.0
        var Latitude = 0.0

        //define the listener for location updates
         val locationListener: LocationListener = object : LocationListener
         {
            override fun onLocationChanged(location: Location)
            {
                Longitude = location.longitude
                Latitude = location.latitude


                // Instantiate the RequestQueue.
                val queue = Volley.newRequestQueue(this@MainActivity)
                val url = "https://api.openweathermap.org/data/2.5/weather?units=imperial&lat=" + Latitude.toString() + "&lon=" + Longitude.toString() + "&appid=926af17a444e5fd61c274302e03d6d66"
                var responseString = ""
// Request a string response from the provided URL.
                val request =
                    JsonObjectRequest(Request.Method.GET, url, null, { response ->
                        try {
                            textView.text = response.getJSONObject("main").getString("temp")
                        } catch (e: JSONException) {
                            textView.text = "failed"
                            e.printStackTrace()
                        }
                    }, { error -> error.printStackTrace() })
                queue.add(request)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        //make the location manager and ask for permissions if not granted
        val locationManager =
            applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //asking for permission here
            val returncode = 1;
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),returncode )
            return
        }
        //specifying our criteria for the best gps provider
        val crit = Criteria();
        crit.accuracy = Criteria.ACCURACY_COARSE

        //retrieve the best from the provider list
        val bestProvider: String = locationManager.getBestProvider(crit, true) as String
        //request a location update and wake the gps
        locationManager.requestLocationUpdates(bestProvider, 0,0.0f,  locationListener)
        //locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)


    }
}