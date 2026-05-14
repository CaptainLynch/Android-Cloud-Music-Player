package com.lynchlin.music.network

import com.lynchlin.music.network.crypto.WeApiCrypto
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class NeteaseDirectInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.method != "POST") {
            return chain.proceed(originalRequest)
        }

        val body = originalRequest.body ?: return chain.proceed(originalRequest)

        if (body.contentType()?.toString()?.contains("multipart") == true) {
            return chain.proceed(originalRequest)
        }

        val buffer = Buffer()
        body.writeTo(buffer)
        val jsonBody = buffer.readUtf8()

        val (encText, encSecKey) = WeApiCrypto.encrypt(jsonBody)

        val formBody = FormBody.Builder()
            .add("params", encText)
            .add("encSecKey", encSecKey)
            .build()

        val newRequest = originalRequest.newBuilder()
            .post(formBody)
            .build()

        return chain.proceed(newRequest)
    }
}
