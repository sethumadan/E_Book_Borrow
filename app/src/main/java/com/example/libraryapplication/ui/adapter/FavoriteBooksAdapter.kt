package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.enums.Availability
import com.example.libraryapplication.ui.utils.LoadImage

class FavoriteBooksAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<FavoriteBooksAdapter.MyViewHolder>() {
    private var favoriteBooks = emptyList<Book>()
    lateinit var view: View
    private lateinit var clickListener: ClickListener

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener=clickListener
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener {
        private var bookImage: ImageView = itemView.findViewById(R.id.shared_book_image_given)
        private var bookName: TextView = itemView.findViewById(R.id.shared_book_name_given)
        private var noOfPages: TextView = itemView.findViewById(R.id.shared_book_pages_given)
        private var subject: TextView = itemView.findViewById(R.id.shared_book_subject_given)
        private var author: TextView = itemView.findViewById(R.id.shared_book_author_given)
        private var availability: TextView = itemView.findViewById(R.id.shared_book_availability)
        private var mainLayout: LinearLayout = itemView.findViewById(R.id.shared_bbok_main_layout)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(favoriteBook: Book) {
            setViewDetails(favoriteBook)
            mainLayout.setOnLongClickListener(this)
            mainLayout.setOnClickListener(this)
            if (favoriteBook.availability == Availability.AVAILABLE.reason) {
                availability.visibility = View.GONE
            } else {
                availability.visibility = View.VISIBLE
            }
        }

        private fun setViewDetails(favoriteBook: Book) {
            LoadImage.loadImage(favoriteBook.bookImage, bookImage)
            bookName.text = favoriteBook.name
            noOfPages.text = favoriteBook.noOfPages.toString()
            subject.text = favoriteBook.subject
            author.text = favoriteBook.authorName
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            clickListener.onClickView(favoriteBooks[position])
        }

        override fun onLongClick(v: View?): Boolean {
            val position=adapterPosition
            clickListener.onLongClickView(favoriteBooks[position])
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        view = layoutInflater.inflate(R.layout.card_shared_book, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(favoriteBooks[position])
    }

    override fun getItemCount(): Int {
        return favoriteBooks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(favoriteBooks: List<Book>) {
        this.favoriteBooks = favoriteBooks
        notifyDataSetChanged()
    }



    interface ClickListener{
        fun onLongClickView(favoriteBook: Book)
        fun onClickView(favoriteBook: Book)
    }
}