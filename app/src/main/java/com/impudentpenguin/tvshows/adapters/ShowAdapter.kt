package com.impudentpenguin.tvshows.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.impudentpenguin.tvshows.R
import com.impudentpenguin.tvshows.data.Show
import com.squareup.picasso.Picasso


class ShowAdapter: RecyclerView.Adapter<ShowAdapter.ShowViewHolder>() {


    interface OnPosterClickListener {
        fun onPosterClick(position: Int)
    }
    interface OnReachEndListener {
        fun onReachEnd()
    }

    var onPosterClickListener: OnPosterClickListener? = null
    var onReachEndListener: OnReachEndListener? = null

    var shows: MutableList<Show>? = null
        set(shows) {
            field = shows
            notifyDataSetChanged()
        }

    init {
        shows =  mutableListOf()
    }


    fun clear() {
        this.shows!!.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ShowViewHolder {
       val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.show_item, viewGroup, false)
       return ShowViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shows?.size ?: 0
    }

    override fun onBindViewHolder(showViewHolder: ShowViewHolder, i: Int) {
        if((shows?.size ?: 0)  >= 20 && i > (shows?.size ?: 0) -4 && onReachEndListener != null) {
            onReachEndListener!!.onReachEnd()
        }
        val show: Show? = shows?.get(i)
        Picasso.get().load(show?.posterPath).into(showViewHolder.imageViewSmallPoster)
    }

    fun addShows(shows: MutableList<Show>) {
        this.shows?.addAll(shows)
        notifyDataSetChanged()
    }


    inner class ShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var imageViewSmallPoster: ImageView? = null

        init {
            imageViewSmallPoster = itemView.findViewById(R.id.iv_smallPoster)
            itemView.setOnClickListener { onPosterClickListener?.onPosterClick(adapterPosition) }
        }
    }


}