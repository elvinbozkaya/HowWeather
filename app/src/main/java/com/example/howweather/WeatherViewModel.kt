package com.example.howweather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> = _weatherData

    private val apiKey = "9d7e09641d3c58f65443bf3102875c60"

    fun fetchWeather(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlString = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
            val url = URL(urlString)
            val connection: HttpURLConnection

            try {
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val data = connection.inputStream.bufferedReader().readText()
                    Log.d("WeatherViewModel", "Weather data: $data")
                    _weatherData.postValue(data)
                } else {
                    Log.e("WeatherViewModel", "Error: ${connection.responseMessage}")
                    _weatherData.postValue("Error: ${connection.responseMessage}")
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Exception: ${e.message}")
                _weatherData.postValue("Exception: ${e.message}")
            }
        }
    }

    fun fetchWeatherForDate(city: String, date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlString = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey"
            val url = URL(urlString)
            val connection: HttpURLConnection

            try {
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val data = connection.inputStream.bufferedReader().readText()
                    Log.d("WeatherViewModel", "Weather data: $data")

                    // Parse JSON to find weather data for the specified date
                    val jsonObject = JSONObject(data)
                    val list = jsonObject.getJSONArray("list")
                    for (i in 0 until list.length()) {
                        val forecast = list.getJSONObject(i)
                        val dtTxt = forecast.getString("dt_txt")
                        if (dtTxt.startsWith(date)) {
                            _weatherData.postValue(forecast.toString())
                            return@launch
                        }
                    }
                } else {
                    Log.e("WeatherViewModel", "Error: ${connection.responseMessage}")
                    _weatherData.postValue("Error: ${connection.responseMessage}")
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Exception: ${e.message}")
                _weatherData.postValue("Exception: ${e.message}")
            }
        }
    }
}
