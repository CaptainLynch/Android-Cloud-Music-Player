package com.lynchlin.music.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NeteaseRetrofitClient {

    private const val DIRECT_BASE_URL = "https://music.163.com/"

    private var currentBaseUrl: String = ""
    private var proxyService: NeteaseApiService? = null
    private var directService: NeteaseOriginApiService? = null
    private var directCookieJar: NeteaseCookieJar? = null

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
    fun getDirectService(): NeteaseOriginApiService {
        if (directService == null) {
            val cookieJar = NeteaseCookieJar()
            directCookieJar = cookieJar

            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .addInterceptor(NeteaseDirectInterceptor())
                .cookieJar(cookieJar)
                .build()

            directService = Retrofit.Builder()
                .baseUrl(DIRECT_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NeteaseOriginApiService::class.java)
        }
        return directService!!
    }

    @Synchronized
    fun getDirectCookieJar(): NeteaseCookieJar? = directCookieJar

    @Synchronized
    fun invalidateDirect() {
        directService = null
        directCookieJar = null
    }

    @Synchronized
    fun invalidate() {
        proxyService = null
        currentBaseUrl = ""
    }
}
