package com.agrisurvey.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agrisurvey.app.R

//class NewsCarouselAdapter(private val newsList: List<String>) :
//    RecyclerView.Adapter<NewsCarouselAdapter.NewsViewHolder>() {
//
//    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val newsText: TextView = itemView.findViewById(R.id.newsText)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_news_card, parent, false)
//        return NewsViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
//        holder.newsText.text = newsList[position]
//    }
//
//    override fun getItemCount(): Int = newsList.size
//}

class NewsCarouselAdapter(private val newsList: List<String>) : RecyclerView.Adapter<NewsCarouselAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.newsTitle.text = newsItem
    }

    override fun getItemCount(): Int = newsList.size

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsTitle: TextView = view.findViewById(R.id.newsText)
    }
}
