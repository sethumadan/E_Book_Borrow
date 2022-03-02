package com.example.libraryapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R

class DistrictsAdapter:
    RecyclerView.Adapter<DistrictsAdapter.MyViewHolder>() {
    private var districts: List<String> = emptyList()
    private lateinit var view: View
    var defaultDistrict = ""
    private lateinit var clickListener: ClickListener

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener=clickListener
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        var districtText: TextView = itemView.findViewById(R.id.drop_down_subject)
        fun bind(district: String) {
            districtText.text = district
            districtText.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            clickListener.onClickDistrict(districts[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.subject_drop_down, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(districts[position])
    }

    override fun getItemCount(): Int {
        return districts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(districts: List<String>) {
        this.districts = districts
        notifyDataSetChanged()
    }

    fun setAdapterDefaultDistrict(district: String) {
        this.defaultDistrict = district
    }

    interface ClickListener{
        fun onClickDistrict(district: String)
    }
}