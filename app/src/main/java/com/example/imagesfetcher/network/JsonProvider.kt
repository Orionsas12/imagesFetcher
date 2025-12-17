package com.example.imagesfetcher.network

import com.example.imagesfetcher.model.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONArray

class JsonProvider (private val client: okhttp3.OkHttpClient) {
    suspend fun fetchJson(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { it.body.string() }
    }

    fun parseJson(jsonString: String): List<ImageItem> {
        val imageList = mutableListOf<ImageItem>()
        try {
            // The provided URL returns a root JSON Array: [{"id":"1", "url":"..."}, ...]
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val item = ImageItem(
                    id = obj.getString("id"),
                    url = obj.getString("imageUrl")
                )
                imageList.add(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageList
    }
}