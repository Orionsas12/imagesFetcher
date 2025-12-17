package com.example.imageloader

interface ImageFetcherClient {
   suspend fun fetchImage(url: String): ByteArray?
}