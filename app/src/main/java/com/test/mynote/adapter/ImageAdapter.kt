package com.test.mynote.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.NoteDetails
import com.test.mynote.R

class ImageAdapter :
    ListAdapter<Bitmap, ImageAdapter.ViewHolder>(NOTES_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_card, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = getItem(position)
        holder.create(image)
        holder.removeButton.setOnClickListener {
            holder.image.setImageDrawable(null)
            it.visibility = View.INVISIBLE
            NoteDetails.deleteImage.value = position
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.note_image)
        val removeButton: ImageButton = view.findViewById(R.id.remove_image)
        fun create(imageUri: Bitmap) {
            image.setImageBitmap(imageUri)
        }
    }


    companion object {
        private val NOTES_COMPARATOR = object : DiffUtil.ItemCallback<Bitmap>() {
            override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
                return false
            }

            override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
                return oldItem == newItem
            }
        }
    }
}

