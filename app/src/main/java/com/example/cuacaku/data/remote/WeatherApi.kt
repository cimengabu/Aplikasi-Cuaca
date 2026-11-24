package com.example.cuacaku.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// =====================================================
// Interface Retrofit untuk Forecast API
// =====================================================
interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day,pressure_msl,cloud_cover,wind_direction_10m,visibility,precipitation,rain",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,precipitation,precipitation_probability,cloud_cover",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset,uv_index_max,precipitation_sum",
        @Query("minutely_15") minutely15: String = "precipitation,weather_code",
        @Query("forecast_minutely_15") forecastMinutely15: Int = 12,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponse
}

// =====================================================
// Interface Retrofit untuk Geocoding API (cari kota)
// =====================================================
interface GeocodingApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en"
    ): GeocodingResponse
}

// =====================================================
// Interface Retrofit untuk Air Quality API
// =====================================================
interface AirQualityApi {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponse
}

// =====================================================
// Interface Retrofit untuk RainViewer Radar API (Peta Radar)
// =====================================================
interface RainViewerApi {
    @GET("public/weather-maps.json")
    suspend fun getWeatherMaps(): RainViewerResponse
}

// =====================================================
// Singleton untuk membuat instance Retrofit
// =====================================================
object NetworkModule {
    /** Retrofit untuk API cuaca (forecast) */
    val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    /** Retrofit untuk API geocoding (cari kota) */
    val geocodingApi: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }

    /** Retrofit untuk API kualitas udara (AQI) */
    val airQualityApi: AirQualityApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://air-quality-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirQualityApi::class.java)
    }

    /** Retrofit untuk API peta radar RainViewer */
    val rainViewerApi: RainViewerApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.rainviewer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RainViewerApi::class.java)
    }
}
