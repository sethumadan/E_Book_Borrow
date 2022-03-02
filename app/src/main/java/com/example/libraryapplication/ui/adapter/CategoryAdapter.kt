package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.Category
import com.example.libraryapplication.ui.utils.LoadImage

class CategoryAdapter :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    private var bookList = emptyList<Book>()
    private var sharedBookList = emptyList<Book>()
    private var imageList = emptyList<String>()
    private var categoryList = emptyList<Category>()
    private lateinit var view: View
    private lateinit var clickListener: ClickListener
    fun setClickListener(clickListener: ClickListener) {
        this.clickListener=clickListener
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        private var sectionImage: ImageView = itemView.findViewById(R.id.section_image)

        fun bind(image: String) {
            sectionImage.setOnClickListener(this)
            LoadImage.loadImage(image, sectionImage)
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            clickListener.onClickView(imageList[position],sharedBookList,categoryList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_categories, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPreviewImageList(previewImageList: List<String>) {
        this.imageList = previewImageList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBookListData(bookList: List<Book>) {
        this.bookList = bookList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCategoryData(categories: List<Category>) {
        this.categoryList = categories
        notifyDataSetChanged()
    }

    fun setDonatedBooks(donatedBookList: List<Book>) {
        this.sharedBookList = donatedBookList
    }

    interface ClickListener{
        fun onClickView(image: String,sharedBookList:List<Book>,categoryBookList:List<Category>)
    }
}