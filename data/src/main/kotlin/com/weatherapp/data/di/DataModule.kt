package com.weatherapp.data.di

import com.weatherapp.core.AppConstants
import com.weatherapp.data.api.WeatherApi
import com.weatherapp.data.repository.CityRepositoryImpl
import com.weatherapp.data.repository.WeatherRepositoryImpl
import com.weatherapp.domain.repository.CityRepository
import com.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder().baseUrl(AppConstants.APP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCityRepository(api: WeatherApi): CityRepository {
        return CityRepositoryImpl(api)
    }
}