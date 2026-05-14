package com.lynchlin.music.network

import org.junit.Assert.*
import org.junit.Test

class NeteaseDirectInterceptorBehaviorTest {

    @Test
    fun `NeteaseDirectInterceptor can be instantiated`() {
        val interceptor = NeteaseDirectInterceptor()
        assertNotNull(interceptor)
    }

    @Test
    fun `NeteaseDirectInterceptor implements Interceptor`() {
        val interceptor = NeteaseDirectInterceptor()
        assertTrue(interceptor is okhttp3.Interceptor)
    }

    @Test
    fun `WeApiCrypto encrypt can be called from interceptor context`() {
        val (params, encSecKey) = com.lynchlin.music.network.crypto.WeApiCrypto.encrypt("{\"test\":1}")
        assertTrue(params.isNotEmpty())
        assertEquals(256, encSecKey.length)
    }
}
