package com.yelloyew.ewaweather.data.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ForecastTypeConverters {

    @TypeConverter
    fun fromMetaInfo(metaInfo: LocalDate): String {
        return metaInfo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    @TypeConverter
    fun toMetaInfo(data: String): LocalDate {
        return LocalDate.parse(data)
    }
}
