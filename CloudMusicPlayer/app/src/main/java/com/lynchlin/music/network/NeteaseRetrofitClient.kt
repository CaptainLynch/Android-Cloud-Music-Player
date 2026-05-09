package com.lynchlin.music.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NeteaseRetrofitClient {
    private var currentBaseUrl: String = ""
    private var service: NeteaseApiService? = null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    @Synchronized
    fun getService(baseUrl: String): NeteaseApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (service == null || normalizedUrl != currentBaseUrl) {
            currentBaseUrl = normalizedUrl
            service = Retrofit.Builder()
                .baseUrl(normalizedUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NeteaseApiService::class.java)
        }
        return service!!
    }

    fun invalidate() {
        service = null
        currentBaseUrl = ""
    }
}
