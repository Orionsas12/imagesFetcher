package com.example.imagesfetcher.network

import com.example.imageloader.ImageFetcherClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpImageFetcher(private val client: OkHttpClient) : ImageFetcherClient {
    override suspend fun fetchImage(url: String): ByteArray? = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { it.body.bytes() }
        } catch (e: Exception) { null }
    }
}