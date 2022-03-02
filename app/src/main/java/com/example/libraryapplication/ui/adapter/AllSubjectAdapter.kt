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
import com.example.libraryapplication.ui.adapter.model.AllSubjectModel

class AllSubjectAdapter :
    RecyclerView.Adapter<AllSubjectAdapter.MyViewHolder>() {
    private var allSubjectModelList: List<AllSubjectModel> = emptyList()
    private lateinit var view: View
    private lateinit var clickListener: ClickListener
    private lateinit var subjectRecycler:SubjectRecycler
    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }
     fun setSubjectRecycler(recycler: SubjectRecycler){
         this.subjectRecycler=recycler
     }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var recyclerView: RecyclerView = itemView.findViewById(R.id.subject_recycler)
        private var subject: TextView = itemView.findViewById(R.id.text_subject)
        private var arrow: ImageView = itemView.findViewById(R.id.subject_arrow)
        private var seeDetails:TextView=itemView.findViewById(R.id.see_details)

        fun bind(allSubjectModel: AllSubjectModel) {
            subject.text = allSubjectModel.subjectName
            subjectRecycler.setSubjectRecycler(recyclerView,allSubjectModel.books)
            arrow.setOnClickListener(this)
            seeDetails.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            clickListener.onClickArrow(allSubjectModelList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_subject, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(allSubjectModelList[position])
    }

    override fun getItemCount(): Int {
        return allSubjectModelList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(allSubjectModel: List<AllSubjectModel>) {
        this.allSubjectModelList = allSubjectModel
        notifyDataSetChanged()
    }

    interface SubjectRecycler{
        fun setSubjectRecycler(recyclerView: RecyclerView,subjectBooks: List<Book>)
    }
    interface ClickListener {
        fun onClickArrow(allSubjectModel: AllSubjectModel)
    }
}