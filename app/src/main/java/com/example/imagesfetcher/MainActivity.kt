package com.example.imagesfetcher

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.imageloader.ImageLoader
import com.example.imagesfetcher.network.JsonProvider
import com.example.imagesfetcher.network.OkHttpImageFetcher
import com.example.imagesfetcher.ui.ImageAdapter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    private val json_provider = JsonProvider(client)
    private lateinit var imageLoader: ImageLoader
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageLoader = ImageLoader(OkHttpImageFetcher(client))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val clearCacheBtn = findViewById<Button>(R.id.clearCacheBtn)


        lifecycleScope.launch {
            val json = json_provider.fetchJson("https://zipoapps-storage-test.nyc3.digitaloceanspaces.com/image_list.json")
            val items = json_provider.parseJson(json)
            adapter = ImageAdapter(items, imageLoader)
            recyclerView.adapter = adapter
        }

        clearCacheBtn.setOnClickListener {
            imageLoader.clearCache(this)
            Toast.makeText(this, "Cache Invalidated", Toast.LENGTH_SHORT).show()
            adapter.notifyDataSetChanged()
        }
    }
}