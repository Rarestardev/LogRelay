package com.rarestardev.logrelay.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var baseUrl: String = ""
    private var authToken: String? = null

    val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(
                Long::class.java,
                JsonSerializer<Long> { src, _, _ ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                    JsonPrimitive(sdf.format(Date(src)))
                },
            )
            .create()
    }

    fun init(url: String, token: String? = null) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
        authToken = token
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                authToken?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        if (baseUrl.isBlank()) {
            throw IllegalStateException("RetrofitClient must be initialized with a base URL before use.")
        }
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val logApiRequest: LogApiRequest by lazy { retrofit.create(LogApiRequest::class.java) }
}