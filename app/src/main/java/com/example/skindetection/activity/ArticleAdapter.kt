package com.example.skindetection.activity

import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skindetection.R
import com.example.skindetection.room.Article
import kotlinx.android.synthetic.main.adapter_main.view.*

class ArticleAdapter(private var articles: ArrayList<Article>, private val listener: OnAdapterListener) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_main, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article, listener)
    }

    class ArticleViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(article: Article, listener: OnAdapterListener) {
            view.text_title.text = article.title
            view.text_author.text = article.author

            // Load image using Glide or Picasso
            Glide.with(view.context)
                .load(article.imageUrl)
                .into(view.image_article)

            // Hide edit and delete icons if user is not logged in
            val isLoggedIn = view.context.getSharedPreferences("loginPrefs", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                view.icon_edit.visibility = View.VISIBLE
                view.icon_delete.visibility = View.VISIBLE
            } else {
                view.icon_edit.visibility = View.GONE
                view.icon_delete.visibility = View.GONE
            }
            view.container_article.setOnClickListener { listener.onClick(article)}

            // Set listener for edit icon only if user is logged in
            if (isLoggedIn) {
                view.icon_edit.setOnClickListener { listener.onUpdate(article) }
                view.icon_delete.setOnClickListener { listener.onDelete(article) }
            }
        }
    }

    fun setData(newList: List<Article>) {
        articles.clear()
        articles.addAll(newList)
        notifyDataSetChanged() // Notify adapter that data has changed
    }

    interface OnAdapterListener {
        fun onClick(article: Article)
        fun onUpdate(article: Article)
        fun onDelete(article: Article)
    }
}