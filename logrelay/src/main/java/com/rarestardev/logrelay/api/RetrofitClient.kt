package com.rarestardev.logrelay.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var baseUrl: String = ""

    fun init(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        if (baseUrl.isBlank()) {
            throw IllegalStateException("RetrofitClient must be initialized with a base URL before use.")
        }
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val logApiRequest: LogApiRequest by lazy { retrofit.create(LogApiRequest::class.java) }
}