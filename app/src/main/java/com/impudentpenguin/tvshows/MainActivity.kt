package com.impudentpenguin.tvshows

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.impudentpenguin.tvshows.adapters.ShowAdapter
import com.impudentpenguin.tvshows.data.MainViewModel
import com.impudentpenguin.tvshows.data.Show
import com.impudentpenguin.tvshows.utils.JSONUtils
import com.impudentpenguin.tvshows.utils.NetworkUtils
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener, LoaderManager.LoaderCallbacks<JSONObject> {

    private lateinit var recyclerViewPosters: RecyclerView
    private lateinit var showAdapter: ShowAdapter
    private lateinit var switchSort: Switch
    private lateinit var textViewTopRated: TextView
    private lateinit var textViewPopularity: TextView
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var viewModel: MainViewModel
    private lateinit var loaderManager: LoaderManager

    private lateinit var lang: String

    private var loaderId = 133
    private var page = 1
    private var isLoading = false
    private var methodOfSort: Int = 0

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels / displayMetrics.density.toInt()
        return if((width / 185) > 2) width / 185 else 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lang = Locale.getDefault().language
        loaderManager = LoaderManager.getInstance(this)
        switchSort = findViewById(R.id.switchSort)
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters)
        textViewTopRated = findViewById(R.id.tv_topRated)
        textViewPopularity = findViewById(R.id.tv_popularity)
        progressBarLoading = findViewById(R.id.progressBarLoading)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        showAdapter = ShowAdapter()

        recyclerViewPosters.layoutManager = GridLayoutManager(this, getColumnCount())
        recyclerViewPosters.adapter = showAdapter

        switchSort.isChecked = true
        switchSort.setOnCheckedChangeListener { _, isChecked ->
            page = 1
            setMethodOfSort(isChecked) }

        switchSort.isChecked = false
        showAdapter.onPosterClickListener = object : ShowAdapter.OnPosterClickListener {
            override fun onPosterClick(position: Int) {
                val show = showAdapter.shows?.get(position)
                val intent = Intent(this@MainActivity,DetailActivity::class.java)
                intent.putExtra("id", show?.id)
                startActivity(intent)
            }
        }

        showAdapter.onReachEndListener = object :  ShowAdapter.OnReachEndListener {
            override fun onReachEnd() {
                if(!isLoading) {
                    downloadData(methodOfSort, page)
                }
            }
        }

        val showFromLiveData = viewModel.shows
        showFromLiveData.observe(this,
            Observer<MutableList<Show>> { shows ->
                if(page == 1) {
                    showAdapter.shows = shows
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.itemMain -> {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.itemFavourite -> {
                val intentFavourite = Intent(this@MainActivity, FavouriteActivity::class.java)
                startActivity(intentFavourite)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<JSONObject> {
       val jsonLoader = NetworkUtils.JSONLoader(this, bundle)
        jsonLoader.onStartLoadingListener = object : NetworkUtils.JSONLoader.OnStartLoadingListener {
            override fun onStartLoading() {
                progressBarLoading.visibility = View.VISIBLE
                isLoading = true
            }
        }
        return jsonLoader
    }

    override fun onLoadFinished(loader: Loader<JSONObject>, jsonObject: JSONObject?) {
        val shows: MutableList<Show> = JSONUtils().getShowFromJSON(jsonObject)
        if(shows.isNotEmpty()) {
            if(page == 1) {
                viewModel.deleteAllShows()
                showAdapter.clear()
            }
            for (show in shows) {
                viewModel.insertShow(show)
            }
            showAdapter.addShows(shows)
            page++
        }
        isLoading = false
        progressBarLoading.visibility = View.INVISIBLE
        loaderManager.destroyLoader(loaderId)
    }

    override fun onLoaderReset(p0: Loader<JSONObject>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.tv_popularity) {
            setMethodOfSort(false)
            switchSort.isChecked = false
            } else {
            setMethodOfSort(true)
            switchSort.isChecked = true
        }
    }

    @Suppress("DEPRECATION")
    private fun setMethodOfSort(isTopRated: Boolean) {
       methodOfSort = if(isTopRated) {
            textViewTopRated.setTextColor(resources.getColor(R.color.colorAccent))
            textViewPopularity.setTextColor(resources.getColor(R.color.colorWhite))
            NetworkUtils().topRated
        } else {
            textViewTopRated.setTextColor(resources.getColor(R.color.colorWhite))
            textViewPopularity.setTextColor(resources.getColor(R.color.colorAccent))
            NetworkUtils().popularity
        }
        downloadData(methodOfSort, page)
    }


    private fun downloadData(methodOfSort: Int, page: Int) {
        val url = NetworkUtils().buildURL(methodOfSort, page, lang)
        val bundle = Bundle()
        bundle.putString("url", url.toString())
        loaderManager.restartLoader(loaderId, bundle, this)
    }

}
