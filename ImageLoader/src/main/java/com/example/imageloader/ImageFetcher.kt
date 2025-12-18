package com.example.imageloader

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageLoader(
    private val fetcher: ImageFetcherClient
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val CACHE_DIR = "image_cache"
    private val EXPIRATION_MS = 4 * 60 * 60 * 1000L

    fun loadImage(context: Context, url: String, placeholderRes: Int, imageView: ImageView) {
        imageView.setImageResource(placeholderRes)

        scope.launch {
            val cachedFile = getFile(context, url)
            val bitmap = withContext(Dispatchers.IO){ // moving file operations to IO dispatcher since we need to search "disk" it might block main thread
                if (cachedFile.exists() && (System.currentTimeMillis() - cachedFile.lastModified() < EXPIRATION_MS)) {
                    BitmapFactory.decodeFile(cachedFile.path)
                } else {
                    null
                }
            }

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            } else {
                withContext(Dispatchers.IO) {
                    try {
                        val bytes = fetcher.fetchImage(url) ?: return@withContext // move this inside withContext(Dispatchers.IO) and surround with try-catch
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        FileOutputStream(cachedFile).use { it.write(bytes) }
                        withContext(Dispatchers.Main) { imageView.setImageBitmap(bitmap) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    fun clearCache(context: Context) {
        CoroutineScope(Dispatchers.IO).launch { // Should be launched in IO dispatcher to avoid blocking main thread if to many files
            val dir = File(context.cacheDir, CACHE_DIR)
            if (dir.exists()) dir.deleteRecursively()
        }
    }

    private fun getFile(context: Context, url: String): File {
        val dir = File(context.cacheDir, CACHE_DIR).apply { mkdirs() }
        return File(dir, url.hashCode().toString())
    }

    companion object {
        @Volatile private var INSTANCE: ImageLoader? = null

        @JvmStatic
        fun getInstance(client: ImageFetcherClient): ImageLoader =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ImageLoader(client).also { INSTANCE = it }
            }
    }
}
