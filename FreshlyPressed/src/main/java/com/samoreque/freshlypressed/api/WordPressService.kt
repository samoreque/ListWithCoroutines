package com.samoreque.freshlypressed.api

import android.net.Uri
import android.util.Log

import java.util.Date

import com.samoreque.freshlypressed.api.converters.DateDeserializer
import com.samoreque.freshlypressed.api.converters.UriDeserializer
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WordPressService {
    companion object {
        private const val BASE_URL = "https://public-api.wordpress.com/rest/v1.1/"
        private const val SERVICE_TAG = "API"
        @JvmStatic
        fun create(): WordPressApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Log.d(SERVICE_TAG, it) })
            logger.level = HttpLoggingInterceptor.Level.BASIC
            val gson = GsonBuilder()
                    .registerTypeAdapter(Uri::class.java, UriDeserializer())
                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                    .create()
            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(WordPressApi::class.java)
        }
    }
}