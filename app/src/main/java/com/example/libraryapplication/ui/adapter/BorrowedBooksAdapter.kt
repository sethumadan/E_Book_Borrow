package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.BorrowDetail
import com.example.libraryapplication.ui.adapter.model.BorrowedBookModel
import com.example.libraryapplication.ui.fragments.args.FromIcon
import com.example.libraryapplication.ui.utils.LoadImage

class BorrowedBooksAdapter :
    RecyclerView.Adapter<BorrowedBooksAdapter.MyViewHolder>() {
    private var borrowedBookList = emptyList<BorrowedBookModel>()
    private var borrowDetails = emptyList<BorrowDetail>()
    lateinit var view: View
    private var fromLocation = ""
    private lateinit var clickListener: ClickListener

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener=clickListener
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener {
        private var bookName: TextView = itemView.findViewById(R.id.card_borrowed_book_name)
        private var bookImageView: ImageView = itemView.findViewById(R.id.card_borrowed_book_image)
        private var borrowDate: TextView =
            itemView.findViewById(R.id.card_borrowed_book_borrowed_date_given)
        private var returnDate: TextView =
            itemView.findViewById(R.id.card_borrowed_book_return_date_given)
        private var dueAmount: TextView = itemView.findViewById(R.id.card_borrowed_book_due_given)
        private var mainLayout: LinearLayout =
            itemView.findViewById(R.id.borrowed_books_main_layout)
        private var returnCard: CardView = itemView.findViewById(R.id.return_card)
        private var returnText: TextView = itemView.findViewById(R.id.return_book)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(borrowedBookModel: BorrowedBookModel, borrowDetail: BorrowDetail) {
            setReturnButton()
            setViewDetails(borrowedBookModel, borrowDetail)
            mainLayout.setOnClickListener(this)
            returnCard.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        private fun setViewDetails(
            borrowedBookModel: BorrowedBookModel,
            borrowDetail: BorrowDetail
        ) {
            LoadImage.loadImage(borrowedBookModel.bookImage, bookImageView)
            bookName.text = borrowedBookModel.bookName
            borrowDate.text = borrowDetail.borrowDate
            returnDate.text = borrowDetail.returnDate
            dueAmount.text = borrowDetail.dueAmount.toString()
        }

        private fun setReturnButton() {
            if (fromLocation != FromIcon.BORROWED_BOOKS.reason) {
                returnCard.visibility = View.VISIBLE
                if (fromLocation == FromIcon.DUE_BOOKS.reason) {
                    returnText.text = "PAY DUE"
                }
            } else {
                returnCard.visibility = View.GONE
            }
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            if(v==returnCard)
           clickListener.onClickReturn(borrowDetails[position],borrowedBookList[position])
            else
                clickListener.onClickView(borrowDetails[position],borrowedBookList[position])

        }

        override fun onLongClick(v: View?): Boolean {
            val position=adapterPosition
            clickListener.onLongClickView(borrowDetails[position], borrowedBookList[position])
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_borrowed_books, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(borrowedBookList[position], borrowDetails[position])
    }

    override fun getItemCount(): Int {
        return borrowDetails.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBorrowDetail(
        borrowDetailList: List<BorrowDetail>,
        borrowedBookList: List<BorrowedBookModel>,
        fromLocation: String
    ) {
        this.borrowDetails = borrowDetailList
        this.borrowedBookList = borrowedBookList
        this.fromLocation = fromLocation
        notifyDataSetChanged()
    }

    interface ClickListener{
        fun onClickReturn(borrowDetail: BorrowDetail, item: BorrowedBookModel)
        fun onClickView(borrowDetail: BorrowDetail, item: BorrowedBookModel)
        fun onLongClickView(borrowDetail: BorrowDetail, item: BorrowedBookModel)
    }
}