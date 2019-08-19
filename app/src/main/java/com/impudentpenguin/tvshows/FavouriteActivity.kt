package com.impudentpenguin.tvshows

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.impudentpenguin.tvshows.adapters.ShowAdapter
import com.impudentpenguin.tvshows.data.FavouriteShow
import com.impudentpenguin.tvshows.data.MainViewModel
import com.impudentpenguin.tvshows.data.Show

class FavouriteActivity : AppCompatActivity() {

    private lateinit var recyclerViewFavouriteShows: RecyclerView
    private lateinit var adapter: ShowAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.itemMain -> {
                val intent = Intent(this@FavouriteActivity, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.itemFavourite -> {
                val intentFavourite = Intent(this@FavouriteActivity, FavouriteActivity::class.java)
                startActivity(intentFavourite)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        recyclerViewFavouriteShows = findViewById(R.id.recyclerViewFavouriteShows)
        recyclerViewFavouriteShows.layoutManager = GridLayoutManager(this, 2)
        adapter = ShowAdapter()
        recyclerViewFavouriteShows.adapter = adapter
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val favouriteShows = viewModel.favouriteShows
        favouriteShows.observe(this,
            Observer<MutableList<FavouriteShow>> { favouriteShowsObserver ->
                if(favouriteShowsObserver != null) {
                    val shows: MutableList<Show> = favouriteShowsObserver.toMutableList()
                    adapter.shows = shows
                }
            })

        adapter.onPosterClickListener = object : ShowAdapter.OnPosterClickListener {
            override fun onPosterClick(position: Int) {
                val show = adapter.shows?.get(position)
                val intent = Intent(this@FavouriteActivity,DetailActivity::class.java)
                intent.putExtra("id", show?.id)
                startActivity(intent)
            }
        }
    }
}
