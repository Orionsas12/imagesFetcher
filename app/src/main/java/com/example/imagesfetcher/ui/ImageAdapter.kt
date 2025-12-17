package com.example.imagesfetcher.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageloader.ImageLoader
import com.example.imageloader.R
import com.example.imagesfetcher.model.ImageItem

class ImageAdapter(
    private val items: List<ImageItem>,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.idTextView.text = "ID: ${item.id}"

        // Use our Library here
        imageLoader.loadImage(
            context = holder.itemView.context,
            url = item.url,
            placeholderRes = R.drawable.placeholder,
            imageView = holder.imageView
        )
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImageView)
        val idTextView: TextView = view.findViewById(R.id.itemIdTextView)
    }
}
