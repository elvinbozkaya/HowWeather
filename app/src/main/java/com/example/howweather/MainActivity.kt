package com.example.howweather

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var cityEditText: EditText
    private lateinit var fetchWeatherButton: Button
    private lateinit var weatherIconImageView: ImageView
    private lateinit var temperatureTextView: TextView
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var cityNameTextView: TextView
    private lateinit var weeklyStatsLayout: LinearLayout
    private lateinit var dateTextView: TextView

    private var currentDay: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityEditText = findViewById(R.id.cityEditText)
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton)
        weatherIconImageView = findViewById(R.id.weatherIconImageView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        weatherDescriptionTextView = findViewById(R.id.weatherDescriptionTextView)
        humidityTextView = findViewById(R.id.humidityTextView)
        windSpeedTextView = findViewById(R.id.windSpeedTextView)
        cityNameTextView = findViewById(R.id.cityNameTextView)
        weeklyStatsLayout = findViewById(R.id.weeklyStatsLayout)
        dateTextView = findViewById(R.id.dateTextView)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherViewModel.weatherData.observe(this, Observer { weather ->
            updateUI(weather)
        })

        fetchWeatherButton.setOnClickListener {
            val city = cityEditText.text.toString()
            weatherViewModel.fetchWeather(city)
        }

        // Initialize date text view
        updateDateTextView()
    }

    private fun fetchWeatherForCurrentDate() {
        val city = cityEditText.text.toString()
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDay.time)
        weatherViewModel.fetchWeatherForDate(city, dateString)
    }

    private fun updateUI(weather: String?) {
        weather?.let {
            try {
                val jsonObject = JSONObject(it)
                val cityName = jsonObject.getString("name")
                val main = jsonObject.getJSONObject("main")
                val temperature = main.getDouble("temp") - 273.15 // Convert Kelvin to Celsius
                val humidity = main.getInt("humidity")
                val weatherArray = jsonObject.getJSONArray("weather")
                val weatherObject = weatherArray.getJSONObject(0)
                val weatherDescription = weatherObject.getString("description")
                val iconCode = weatherObject.getString("icon")
                val wind = jsonObject.getJSONObject("wind")
                val windSpeed = wind.getDouble("speed")

                // Update UI elements
                val iconResource = getIconResource(iconCode)
                weatherIconImageView.setImageResource(iconResource)
                temperatureTextView.text = "%.1f°C".format(temperature)
                weatherDescriptionTextView.text = weatherDescription.capitalize()
                humidityTextView.text = "Humidity: $humidity%"
                windSpeedTextView.text = "Wind Speed: $windSpeed m/s"
                cityNameTextView.text = cityName

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getIconResource(iconCode: String): Int {
        return when (iconCode) {
            "01d", "01n" -> R.drawable.clear_sky
            "02d", "02n" -> R.drawable.few_clouds
            "03d", "03n" -> R.drawable.scattered_clouds
            "04d", "04n" -> R.drawable.broken_clouds
            "09d", "09n" -> R.drawable.shower_rain
            "10d", "10n" -> R.drawable.rain
            "11d", "11n" -> R.drawable.thunderstorm
            "13d", "13n" -> R.drawable.snow
            "50d", "50n" -> R.drawable.mist
            else -> R.drawable.ic_launcher_background // Default icon
        }
    }

    private fun updateDateTextView() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDay.time)
    }
}

