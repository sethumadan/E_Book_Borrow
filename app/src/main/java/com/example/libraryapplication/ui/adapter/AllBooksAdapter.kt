package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.ui.utils.LoadImage


class AllBooksAdapter  :
    RecyclerView.Adapter<AllBooksAdapter.MyViewHolder>() {
    private var bookList = emptyList<Book>()
    lateinit var view: View
    private lateinit var clickListener: ClickListener

    fun setClickListener(clickListener: ClickListener) {
         this.clickListener=clickListener
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        private var bookImage: ImageView = itemView.findViewById(R.id.book_image)
        private var bookName: TextView = itemView.findViewById(R.id.book_name)
        private var author: TextView = itemView.findViewById(R.id.author)
        private var subject: TextView = itemView.findViewById(R.id.subject)

        fun bind(book: Book) {
            LoadImage.loadImage(book.bookImage, bookImage)
            setViewDetails(book)
            view.setOnClickListener(this)
        }

        private fun setViewDetails(book: Book) {
            bookName.text = book.name
            author.text = book.authorName
            subject.text = book.subject
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            clickListener.onBookClicked(bookList[position],itemView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_all_books, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(bookList[position])
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(books: List<Book>) {
        this.bookList = books
        notifyDataSetChanged()
    }
    interface ClickListener {
        fun onBookClicked(book:Book,itemView: View)
    }
}
