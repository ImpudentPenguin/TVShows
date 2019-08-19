package com.impudentpenguin.tvshows.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore

@Entity(tableName = "favourite_shows")
class FavouriteShow : Show {

  constructor(uniqueId: Int,id: Int, voteCount: Int, name: String, originalName: String, overview: String, posterPath: String,
              bigPosterPath: String, backdropPath: String, voteAverage: Double, firstAirDate: String) :
  super(uniqueId, id, voteCount, name, originalName, overview, posterPath, bigPosterPath, backdropPath, voteAverage, firstAirDate)

    @Ignore
    constructor (show: Show) :
    super( show.uniqueId,
           show.id,
           show.voteCount,
           show.name,
           show.originalName,
           show.overview,
           show.posterPath,
           show.bigPosterPath,
           show.backdropPath,
           show.voteAverage,
           show.firstAirDate)

}