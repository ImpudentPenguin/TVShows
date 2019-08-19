package com.impudentpenguin.tvshows.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.impudentpenguin.tvshows.R
import com.impudentpenguin.tvshows.data.Review

class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    var reviews: ArrayList<Review>? = null
        set(reviews) {
            field = reviews
            notifyDataSetChanged()
        }



    override fun getItemCount(): Int {
        return reviews?.size ?: 0
    }

    override fun onBindViewHolder(reviewViewHolder: ReviewViewHolder, i: Int) {
        val review = reviews?.get(i)
        reviewViewHolder.textViewContent?.text = review?.content
        reviewViewHolder.textViewAuthor?.text = review?.author
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ReviewViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.review_item, viewGroup, false)
        return ReviewViewHolder(view)
    }

    inner class ReviewViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textViewAuthor: TextView? = null
        internal var textViewContent: TextView? = null

        init {
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor)
            textViewContent = itemView.findViewById(R.id.textViewContent)
        }
    }

}