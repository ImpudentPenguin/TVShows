package com.impudentpenguin.tvshows.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = [Show::class, FavouriteShow::class], version = 6, exportSchema = false)
abstract class ShowDatabase: RoomDatabase() {

    abstract fun showDao(): ShowDao

    companion object{
        private val dbName: String = "shows.db"
        private var database: ShowDatabase? = null
        private var lock = Object()

        fun getInstance(context: Context): ShowDatabase {
            synchronized(lock) {
            if(database == null) {
                database = Room.databaseBuilder(
                    context,
                    ShowDatabase::class.java,
                    dbName
                ).fallbackToDestructiveMigration().build()
            }

            return database as ShowDatabase

            }
        }
    }

}