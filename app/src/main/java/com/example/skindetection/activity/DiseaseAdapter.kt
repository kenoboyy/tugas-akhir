package com.example.skindetection.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skindetection.R
import com.example.skindetection.room.Disease
import kotlinx.android.synthetic.main.adapter_disease.view.*

class DiseaseAdapter(private var diseases: ArrayList<Disease>, private val listener: OnAdapterListener) :
    RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_disease, parent, false)
        return DiseaseViewHolder(view)
    }

    override fun getItemCount() = diseases.size

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        val disease = diseases[position]
        holder.bind(disease, listener)
    }

    class DiseaseViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(disease: Disease, listener: OnAdapterListener) {
            view.text_name.text = disease.name
            view.text_about.text = disease.about

            // Load image using Glide or Picasso
            Glide.with(view.context)
                .load(disease.med_img)
                .into(view.image_disease)

            view.container_disease.setOnClickListener { listener.onClick(disease) }
            view.icon_edit_disease.setOnClickListener { listener.onUpdate(disease) }
//            view.icon_delete_disease.setOnClickListener { listener.onDelete(disease) }
        }
    }

    fun setData(newList: List<Disease>) {
        diseases.clear()
        diseases.addAll(newList)
        notifyDataSetChanged() // Notify adapter that data has changed
    }

    interface OnAdapterListener {
        fun onClick(disease: Disease)
        fun onUpdate(disease: Disease)
        fun onDelete(disease: Disease)
    }
}