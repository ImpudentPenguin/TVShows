package com.impudentpenguin.tvshows.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.impudentpenguin.tvshows.R
import com.impudentpenguin.tvshows.data.Trailer

class TrailerAdapter: RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {

    interface OnTrailerClickListener {
        fun onTrailerClick(url: String)
    }

    var trailers: ArrayList<Trailer>? = null
        set(trailers) {
            field = trailers
            notifyDataSetChanged()
        }
    var onTrailerClickListener: OnTrailerClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TrailerViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.trailer_item, viewGroup, false)
        return TrailerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trailers?.size ?: 0
    }

    override fun onBindViewHolder(trailerViewHolder: TrailerViewHolder, i: Int) {
        val trailer = trailers?.get(i)
        trailerViewHolder.textViewNameOfVideo?.text = trailer?.nameVideo
    }

    inner class TrailerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textViewNameOfVideo: TextView? = null

        init {
            textViewNameOfVideo = itemView.findViewById(R.id.textViewNameOfVideo)
            itemView.setOnClickListener {
                if(onTrailerClickListener != null) {
                    onTrailerClickListener!!.onTrailerClick(trailers!![adapterPosition].key)
                }
            }
        }
    }
}