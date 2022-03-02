package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.fragments.SubjectFragmentDirections
import com.example.libraryapplication.ui.utils.LoadImage

class SubjectsAdapter :
    RecyclerView.Adapter<SubjectsAdapter.MyViewHolder>() {
    private var subjectList = emptyList<Book>()
    lateinit var view: View

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var bookImage: ImageView = itemView.findViewById(R.id.subject_image)
        private var mainLayout: LinearLayout =
            itemView.findViewById(R.id.card_subjects_lmain_layout)

        fun bind(book: Book) {
            LoadImage.loadImage(book.bookImage, bookImage)
            onClickView(book)
        }

        private fun onClickView(book: Book) {
            mainLayout.setOnClickListener {
                itemView.findNavController().safeNavigate(
                    SubjectFragmentDirections.actionSubjectFragmentToBookInfo(book)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_subjects, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subjectList[position])
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(subjects: List<Book>) {
        this.subjectList = subjects
        notifyDataSetChanged()
    }
}