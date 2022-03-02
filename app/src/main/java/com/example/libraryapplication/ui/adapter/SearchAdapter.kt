package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R

class SearchAdapter(private val onSubjectChecked: OnSubjectChecked) :
    RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {
    private var subjectList = emptyList<String>()
    private  var items= mutableListOf<Boolean>()
    private lateinit var view: View
    private var selectedList = arrayListOf<String>()


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subjectBox: CheckBox = itemView.findViewById(R.id.subject_check)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.card_search_drop_down, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.subjectBox.text = subjectList[position]
        if (!items[position]) holder.subjectBox.isChecked = false
        holder.subjectBox.isChecked = items[position]
        holder.subjectBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                items[position] = isChecked
                if (holder.subjectBox.isChecked) {
                    if (!selectedList.contains(subjectList[position])) {
                        selectedList.add(subjectList[position])
                        onSubjectChecked.onChecked(subjectList[position],items[position])
                    }
                } else {
                    if (selectedList.contains(subjectList[position])) {
                        selectedList.remove(subjectList[position])
                        onSubjectChecked.onChecked(subjectList[position],items[position])
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(subjects: List<String>, states: MutableList<Boolean>) {
        this.subjectList = subjects
        this.items = states
        notifyDataSetChanged()
    }
    fun interface OnSubjectChecked{
        fun onChecked(subject: String, checked:Boolean)
    }
}
