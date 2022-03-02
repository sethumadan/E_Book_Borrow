package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.enums.Availability
import com.example.libraryapplication.ui.adapter.model.MostBorrowedAdapterModel
import com.example.libraryapplication.ui.utils.LoadImage

class MostBorrowedListAdapter:
    RecyclerView.Adapter<MostBorrowedListAdapter.MyViewHolder>() {
    private lateinit var view: View
    private var mostBorrowedBookList = emptyList<MostBorrowedAdapterModel>()
    private lateinit var clickListener: ClickListener

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener=clickListener
    }
   inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        private var bookImage: ImageView =
            itemView.findViewById(R.id.most_borrowed_book_image_given)
        private var bookName: TextView = itemView.findViewById(R.id.most_borrowed_book_name_given)
        private var author: TextView = itemView.findViewById(R.id.most_borrowed_book_author_given)
        private var subject: TextView = itemView.findViewById(R.id.most_borrowed_book_subject_given)
        private var noOfBorrows: TextView = itemView.findViewById(R.id.no_of_borrows_given_info)
        private var availability: TextView =
            itemView.findViewById(R.id.most_borrowed_book_availability)
         private var mainLayout:LinearLayout=itemView.findViewById(R.id.main_layout_most_borrowed)
        fun bind(mostBorrowedBook: MostBorrowedAdapterModel) {
            mainLayout.setOnClickListener(this)
            setViewDetails(mostBorrowedBook)
            if (mostBorrowedBook.book.availability == Availability.AVAILABLE.reason) {
                availability.visibility = View.GONE
            } else {
                availability.visibility = View.VISIBLE
            }
        }

        private fun setViewDetails(mostBorrowedBook: MostBorrowedAdapterModel) {
            LoadImage.loadImage(mostBorrowedBook.book.bookImage, bookImage)
            bookName.text = mostBorrowedBook.book.name
            author.text = mostBorrowedBook.book.authorName
            subject.text = mostBorrowedBook.book.subject
            noOfBorrows.text = mostBorrowedBook.noOfBorrows.toString()
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
                clickListener.onClickView(mostBorrowedBookList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_most_borrowed, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mostBorrowedBookList[position])
    }

    override fun getItemCount(): Int {
        return mostBorrowedBookList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(mostBorrowedBookList: List<MostBorrowedAdapterModel>) {
        this.mostBorrowedBookList = mostBorrowedBookList
        notifyDataSetChanged()
    }
    interface ClickListener{
        fun onClickView(mostBorrowedBook: MostBorrowedAdapterModel)
    }
}