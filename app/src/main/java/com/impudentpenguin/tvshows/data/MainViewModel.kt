package com.impudentpenguin.tvshows.data

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import java.util.concurrent.ExecutionException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
      lateinit var database: ShowDatabase
    }

    val shows: LiveData<MutableList<Show>>
    val favouriteShows: LiveData<MutableList<FavouriteShow>>

    init {
        database = ShowDatabase.getInstance(getApplication())
        shows = database.showDao().getAllShows()
        favouriteShows = database.showDao().getAllFavouriteShows()

    }

    fun getShowById(id: Int): Show? {
        try {
            return GetShowTask().execute(id).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return null
    }

    fun getFavouriteShowById(id: Int): FavouriteShow? {
        try {
            return GetFavouriteShowTask().execute(id).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return null
    }

    fun deleteAllShows() {
        DeleteShowsTask().execute()
    }

    fun insertShow(show: Show) {
        InsertTask().execute(show)
    }

    fun deleteShow(show: Show) {
        DeleteTask().execute(show)
    }


    fun insertFavouriteShow(show: FavouriteShow) {
        InsertFavouriteTask().execute(show)
    }

    fun deleteFavouriteShow(show: FavouriteShow) {
        DeleteFavouriteTask().execute(show)
    }

    private class DeleteTask: AsyncTask<Show, Unit, Unit>() {
        override fun doInBackground(vararg shows: Show?) {
            if(shows.isNotEmpty()) {
                database.showDao().deleteShow(shows[0]!!)
            }
        }
    }

    private class InsertTask: AsyncTask<Show, Unit, Unit>() {
        override fun doInBackground(vararg shows: Show?) {
            if(shows.isNotEmpty()) {
                database.showDao().insertShow(shows[0]!!)
            }
        }
    }


    private class DeleteFavouriteTask: AsyncTask<FavouriteShow, Unit, Unit>() {
        override fun doInBackground(vararg shows: FavouriteShow?) {
            if(shows.isNotEmpty()) {
                database.showDao().deleteFavouriteShow(shows[0]!!)
            }
        }
    }

    private class InsertFavouriteTask: AsyncTask<FavouriteShow, Unit, Unit>() {
        override fun doInBackground(vararg shows: FavouriteShow?) {
            if(shows.isNotEmpty()) {
                database.showDao().insertFavouriteShow(shows[0]!!)
            }
        }
    }

    private class DeleteShowsTask: AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg integers: Unit?) {
                database.showDao().deleteAllShows()
         }
    }

    private class GetShowTask: AsyncTask<Int, Unit, Show>() {
        override fun doInBackground(vararg integers: Int?): Show? {
            if(integers.isNotEmpty()) {
                return database.showDao().getShowById(integers[0]!!)
            }
            return null
        }
    }

    private class GetFavouriteShowTask: AsyncTask<Int, Unit, FavouriteShow>() {
        override fun doInBackground(vararg integers: Int?): FavouriteShow? {
            if(integers.isNotEmpty()) {
                return database.showDao().getFavouriteShowById(integers[0]!!)
            }
            return null
        }
    }


}