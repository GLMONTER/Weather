package com.example.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.os.Bundle
import android.widget.TextView

//for http requests
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

//for json parsing
import org.json.JSONException
import org.w3c.dom.Text

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get the textboxes
        val locationTextBox = findViewById<TextView>(R.id.locationText)
        val conditionsTextBox = findViewById<TextView>(R.id.conditionsText)
        val temperatureTextBox = findViewById<TextView>(R.id.temperatureText)
        val feelsTextBox = findViewById<TextView>(R.id.feelText)
        val lowTextBox = findViewById<TextView>(R.id.lowText)
        val highTextBox = findViewById<TextView>(R.id.highText)
        val windTextBox = findViewById<TextView>(R.id.windText)
        val humidTextBox = findViewById<TextView>(R.id.humidText)




        //global storage for location data
        var Longitude : Double
        var Latitude : Double

        //define the listener for location updates
         val locationListener: LocationListener = object : LocationListener
         {
            override fun onLocationChanged(location: Location)
            {

                Longitude = location.longitude
                Latitude = location.latitude
                var cityName : String
                var geoCoder = Geocoder(this@MainActivity)
                cityName = geoCoder.getFromLocation(Latitude, Longitude, 1)[0].locality
                locationTextBox.text = cityName

                // Instantiate the RequestQueue.
                val queue = Volley.newRequestQueue(this@MainActivity)
                val url = "https://api.openweathermap.org/data/2.5/weather?units=imperial&lat=" + Latitude.toString() + "&lon=" + Longitude.toString() + "&appid=926af17a444e5fd61c274302e03d6d66"
                // Request a string response from the provided URL.
                val request =
                    JsonObjectRequest(Request.Method.GET, url, null, { response ->
                        try {

                            var formatString = response.getJSONArray("weather").getJSONObject(0).getString("description").split(" ").joinToString(" ") { it.replaceFirstChar { it.uppercase() } }.trimEnd();

                            conditionsTextBox.text = formatString
                            temperatureTextBox.text = "Temp(F) : " + response.getJSONObject("main").getString("temp")
                            feelsTextBox.text = "Feels like (F) : " + response.getJSONObject("main").getString("feels_like")
                            lowTextBox.text = "Low (F) : " + response.getJSONObject("main").getString("temp_min")
                            highTextBox.text = "High (F) : " + response.getJSONObject("main").getString("temp_max")
                            windTextBox.text = "Wind Speed (MPH) : " + response.getJSONObject("wind").getString("speed")
                            humidTextBox.text = "Humidity (%) : " + response.getJSONObject("main").getString("humidity")



                        } catch (e: JSONException) {
                        //    textView.text = "failed"
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