package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.enums.Availability
import com.example.libraryapplication.ui.utils.LoadImage

class SharedBooksAdapter :
    RecyclerView.Adapter<SharedBooksAdapter.MyViewHolder>() {
    private var sharedBooks = emptyList<Book>()
    lateinit var view: View
    private lateinit var clickListener: ClickListener

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener=clickListener
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        var bookImage: ImageView = itemView.findViewById(R.id.shared_book_image_given)
        var bookName: TextView = itemView.findViewById(R.id.shared_book_name_given)
        var pages: TextView = itemView.findViewById(R.id.shared_book_pages_given)
        var subject: TextView = itemView.findViewById(R.id.shared_book_subject_given)
        private var author: TextView = itemView.findViewById(R.id.shared_book_author_given)
        var getBackButton: TextView = itemView.findViewById(R.id.shared_book_get_back_button)
        var availability: TextView = itemView.findViewById(R.id.shared_book_availability)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(sharedBook: Book) {
         setViewDetails(sharedBook)
            if (sharedBook.availability == Availability.AVAILABLE.reason) {
                getBackButton.visibility = View.VISIBLE
                availability.visibility = View.GONE
                getBackButton.setOnClickListener(this)
            } else {
                availability.visibility = View.VISIBLE
                getBackButton.visibility = View.GONE
            }
        }

        private fun setViewDetails(sharedBook: Book) {
            LoadImage.loadImage(sharedBook.bookImage,bookImage)
            bookName.text = sharedBook.name
            pages.text = sharedBook.noOfPages.toString()
            subject.text = sharedBook.subject
            author.text = sharedBook.authorName
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            clickListener.onClickGetBack(sharedBooks[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_shared_book, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(sharedBooks[position])
    }

    override fun getItemCount(): Int {
        return sharedBooks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(sharedBooks: List<Book>) {
        this.sharedBooks = sharedBooks
        notifyDataSetChanged()
    }


interface ClickListener{
    fun onClickGetBack(sharedBook: Book)
}
}