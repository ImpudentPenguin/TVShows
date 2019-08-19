package com.impudentpenguin.tvshows.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "shows")
open class Show {
    @PrimaryKey(autoGenerate = true)
    var uniqueId: Int = 0
    var id: Int
    var voteCount: Int
    var name: String
    var originalName: String
    var overview: String
    var posterPath: String
    var bigPosterPath: String
    var backdropPath: String
    var voteAverage: Double
    var firstAirDate: String

    constructor(uniqueId: Int, id: Int, voteCount: Int, name: String, originalName: String, overview: String,
                posterPath: String, bigPosterPath: String, backdropPath: String, voteAverage: Double,
                firstAirDate: String) {
        this.uniqueId = uniqueId
        this.id = id
        this.voteCount = voteCount
        this.name = name
        this.originalName = originalName
        this.overview = overview
        this.posterPath = posterPath
        this.bigPosterPath = bigPosterPath
        this.backdropPath = backdropPath
        this.voteAverage = voteAverage
        this.firstAirDate = firstAirDate
    }

    @Ignore
    constructor(id: Int, voteCount: Int, name: String, originalName: String, overview: String, posterPath: String,
                bigPosterPath: String, backdropPath: String, voteAverage: Double, firstAirDate: String) {
        this.id = id
        this.voteCount = voteCount
        this.name = name
        this.originalName = originalName
        this.overview = overview
        this.posterPath = posterPath
        this.bigPosterPath = bigPosterPath
        this.backdropPath = backdropPath
        this.voteAverage = voteAverage
        this.firstAirDate = firstAirDate
    }

}