package com.lynchlin.music.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NeteaseRetrofitClient {

    private var currentBaseUrl: String = ""
    private var proxyService: NeteaseApiService? = null

    private val baseOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    @Synchronized
    fun getProxyService(baseUrl: String): NeteaseApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (proxyService == null || normalizedUrl != currentBaseUrl) {
            currentBaseUrl = normalizedUrl
            proxyService = Retrofit.Builder()
                .baseUrl(normalizedUrl)
                .client(baseOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NeteaseApiService::class.java)
        }
        return proxyService!!
    }

    @Synchronized
    fun invalidate() {
        proxyService = null
        currentBaseUrl = ""
    }
}
