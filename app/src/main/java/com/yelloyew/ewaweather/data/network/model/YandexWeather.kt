package com.yelloyew.ewaweather.data.network.model

data class YandexWeather(
    val forecasts: Array<YandexForecasts>
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YandexWeather

        if (!forecasts.contentEquals(other.forecasts)) return false

        return true
    }

    override fun hashCode(): Int {
        return forecasts.contentHashCode()
    }
}