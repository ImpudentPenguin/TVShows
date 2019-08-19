package com.impudentpenguin.tvshows

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.impudentpenguin.tvshows.adapters.ReviewAdapter
import com.impudentpenguin.tvshows.adapters.TrailerAdapter
import com.impudentpenguin.tvshows.data.FavouriteShow
import com.impudentpenguin.tvshows.data.MainViewModel
import com.impudentpenguin.tvshows.data.Show
import com.impudentpenguin.tvshows.utils.JSONUtils
import com.impudentpenguin.tvshows.utils.NetworkUtils
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity() {

    private lateinit var imageViewAddToFavourite: ImageView
    private lateinit var imageViewBigPoster: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewOriginalTitle: TextView
    private lateinit var textViewRating: TextView
    private lateinit var textViewReleaseDate: TextView
    private lateinit var textViewOverview: TextView
    private lateinit var scrollViewInfo: ScrollView
    private lateinit var recyclerViewTrailers: RecyclerView
    private lateinit var recyclerViewReviews: RecyclerView
    private lateinit var textViewNextEpisodes: TextView
    private lateinit var textViewSeasons: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var textViewGenres: TextView

    private lateinit var trailerAdapter: TrailerAdapter
    private lateinit var reviewAdapter: ReviewAdapter

    private lateinit var textViewLabelReviews: TextView
    private lateinit var textViewLabelTrailers: TextView
    private lateinit var textViewNoReviews: TextView
    private lateinit var textViewNoTrailers: TextView

    private lateinit var lang: String

    private var id: Int = 0
    private lateinit var show: Show
    private var favouriteShow: FavouriteShow? = null

    private lateinit var viewModel: MainViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id) {
            R.id.itemMain -> {
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.itemFavourite -> {
                val intentFavourite = Intent(this@DetailActivity, FavouriteActivity::class.java)
                startActivity(intentFavourite)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        lang = Locale.getDefault().language

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster)
        textViewTitle = findViewById(R.id.textViewTitle)
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle)
        textViewRating = findViewById(R.id.textViewRating)
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate)
        textViewOverview = findViewById(R.id.textViewOverview)
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite)
        scrollViewInfo = findViewById(R.id.scrollViewInfo)
        textViewStatus = findViewById(R.id.textViewStatus)
        textViewNextEpisodes = findViewById(R.id.textViewNextEpisode)
        textViewSeasons = findViewById(R.id.textViewSeasons)
        textViewGenres = findViewById(R.id.textViewGenre)
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews)
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers)
        textViewNoReviews = findViewById(R.id.textViewNoReviews)
        textViewNoTrailers = findViewById(R.id.textViewNoTrailers)
        textViewLabelReviews = findViewById(R.id.textViewLabelReviews)
        textViewLabelTrailers = findViewById(R.id.textViewLabelTrailers)

        val intent = intent
        if(intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1)
        } else finish()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        show = viewModel.getShowById(id)!!

        val dateFirstAirDate = show.firstAirDate

        Picasso.get().load(show.bigPosterPath).placeholder(R.drawable.poster_default).into(imageViewBigPoster)
        textViewTitle.text = show.name
        textViewOriginalTitle.text = show.originalName
        textViewOverview.text = show.overview
        textViewReleaseDate.text = formatDate(dateFirstAirDate)
        textViewRating.text = String.format("%s / 10", show.voteAverage.toString())
        setFavourite()

        reviewAdapter = ReviewAdapter()
        trailerAdapter = TrailerAdapter()

        trailerAdapter.onTrailerClickListener = object : TrailerAdapter.OnTrailerClickListener {
            override fun onTrailerClick(url: String) {
                val intentToTrailer = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intentToTrailer)
            }
        }

        recyclerViewReviews.layoutManager = LinearLayoutManager(this)
        recyclerViewTrailers.layoutManager = LinearLayoutManager(this)

        recyclerViewReviews.adapter = reviewAdapter
        recyclerViewTrailers.adapter = trailerAdapter

        val jsonObjectTrailers = NetworkUtils().getJSONForVideos(show.id, lang)
        val jsonObj = NetworkUtils().getJSONForMoreInfo(show.id, lang)
        val jsonObjectReviews = NetworkUtils().getJSONForReviews(show.id)
        val trailers  = JSONUtils().getTrailersFromJSON(jsonObjectTrailers)
        val reviews = JSONUtils().getReviewsFromJSON(jsonObjectReviews)
        val moreInfo = JSONUtils().getMoreInfoFromJSON(jsonObj)

        if(moreInfo[0].nextEpisode != "Дата выхода неизвестна" && moreInfo[0].nextEpisode != "сериал закрыт") {
            textViewNextEpisodes.text = formatDate(moreInfo[0].nextEpisode)
        } else {
            textViewNextEpisodes.text = moreInfo[0].nextEpisode
        }
        textViewStatus.text = moreInfo[0].status
        textViewGenres.text = moreInfo[0].genres
        textViewSeasons.text = String.format("%s %s", moreInfo[0].seasons, getString(R.string.seasons_string))

        reviewAdapter.reviews = reviews
        trailerAdapter.trailers = trailers

        if(reviews.isEmpty()) {
            textViewNoReviews.visibility = View.VISIBLE
            textViewLabelReviews.visibility = View.INVISIBLE
        } else textViewNoReviews.visibility = View.INVISIBLE

        if(trailers.isEmpty()) {
            textViewNoTrailers.visibility = View.VISIBLE
            textViewLabelTrailers.visibility = View.INVISIBLE
        } else textViewNoTrailers.visibility = View.INVISIBLE

        scrollViewInfo.smoothScrollTo(0,0)

    }

    fun onClickChangeFavourite(view: View) {
        if(favouriteShow == null) {
            viewModel.insertFavouriteShow(FavouriteShow(show))
            Toast.makeText(this, getString(R.string.add_to_favourite), Toast.LENGTH_SHORT).show()
        } else {
            viewModel.deleteFavouriteShow(favouriteShow!!)
            Toast.makeText(this, getString(R.string.remove_from_favourite), Toast.LENGTH_SHORT).show()
        }
        setFavourite()
    }

    private fun setFavourite() {
        favouriteShow = viewModel.getFavouriteShowById(id)
        if(favouriteShow == null) imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to)
        else imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove)
    }

    private fun formatDate(fullDate: String): String {
        val dateFormat = SimpleDateFormat("d MMM yyyy")
        val year = fullDate.substring(0,4)
        val month = fullDate.substring(5,7)
        val day = fullDate.substring(8,10)
        val date = GregorianCalendar(year.toInt(), month.toInt()-1, day.toInt())
        return dateFormat.format(date.time)
    }

}
