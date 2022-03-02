package com.example.libraryapplication.ui.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso

object LoadImage {
    fun loadImage(image:String,view: ImageView){
        if (image.startsWith("http", false))
            Picasso.get().load(image).into(view)
        else {
            val finalImage= BitmapConverter.convert(image)
            view .setImageBitmap(finalImage)
        }
    }
}