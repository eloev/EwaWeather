package com.yelloyew.ewaweather.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yelloyew.ewaweather.domain.model.Forecast

@Database(entities = [ Forecast::class], version=1)
@TypeConverters(ForecastTypeConverters::class)
abstract class ForecastDatabase : RoomDatabase() {

    abstract fun forecastDao() : ForecastDAO
}