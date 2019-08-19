package com.impudentpenguin.tvshows.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface ShowDao {
    @Query("SELECT * FROM shows")
    fun getAllShows(): LiveData<MutableList<Show>>

    @Query("SELECT * FROM favourite_shows")
    fun getAllFavouriteShows(): LiveData<MutableList<FavouriteShow>>

    @Query("SELECT * FROM shows WHERE id == :showsId")
    fun getShowById(showsId: Int) : Show

    @Query("SELECT * FROM favourite_shows WHERE id == :showsId")
    fun getFavouriteShowById(showsId: Int) : FavouriteShow

    @Query("DELETE FROM shows")
    fun deleteAllShows()

    @Insert
    fun insertShow(show: Show)

    @Delete
    fun deleteShow(show: Show)

    @Insert
    fun insertFavouriteShow(show: FavouriteShow)

    @Delete
    fun deleteFavouriteShow(show: FavouriteShow)
}