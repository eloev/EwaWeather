package com.yelloyew.ewaweather.data.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.yelloyew.ewaweather.domain.model.Forecast
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import java.time.LocalDate
import javax.inject.Inject

private const val DATABASE_NAME = "saved_forecasts"
private const val TAG = "tag15 Repository"

@BoundTo(supertype = ForecastRepository::class, component = SingletonComponent::class)
class ForecastRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ForecastRepository {

    init {
        Log.d(TAG, "repository created")
    }

    private val database: ForecastDatabase = Room.databaseBuilder(
        context.applicationContext,
        ForecastDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val forecastDao = database.forecastDao()

    override suspend fun getForecasts() : MutableList<Forecast> = forecastDao.getForecasts()

    override suspend fun addForecast(forecast: Forecast) = forecastDao.addForecast(forecast)

    override suspend fun deleteForecast(date: LocalDate) = forecastDao.deleteForecast(date)

    override suspend fun updateForecast(forecast: Forecast) = forecastDao.updateForecast(forecast)
}