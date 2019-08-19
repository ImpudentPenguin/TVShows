package com.impudentpenguin.tvshows.utils

import android.util.Log
import com.impudentpenguin.tvshows.data.MoreInfo
import com.impudentpenguin.tvshows.data.Review
import com.impudentpenguin.tvshows.data.Show
import com.impudentpenguin.tvshows.data.Trailer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class JSONUtils {

     /** Keys JSON **/
    private val keyResults = "results"

        /** Keys Reviews **/
    private val keyAuthor = "author"
    private val keyContent = "content"

        /** Keys Videos **/
    private val keyKeyOfVideo = "key"
    private val keyNameVideo = "name"
    private val baseYoutubeURL = "https://www.youtube.com/watch?v="


        /** Keys Info on shows **/
    private val keyVoteCount = "vote_count"
    private val keyId = "id"
    private val keyName = "name"
    private val keyOriginalName = "original_name"
    private val keyOverview = "overview"
    private val keyPosterPath = "poster_path"
    private val keyBackdropPath = "backdrop_path"
    private val keyVoteAverage = "vote_average"
    private val keyFirstAirDate = "first_air_date"

    /** Images **/
    private val basePosterURL = "https://image.tmdb.org/t/p/"
    private val smallPosterSize = "w185"
    private val bigPosterSize = "w780"

    /**Keys more info**/
    private val keyGenres = "genres"
    private var genresArray = StringBuilder()
    private val keyStatus = "status"
    private val keyNextEpisode = "next_episode_to_air"
    private val keyDateNextEpisode = "air_date"
    private val keySeasons = "seasons"
    private val keySeasonsNumber = "season_number"


    fun getReviewsFromJSON(jsonObject: JSONObject?): ArrayList<Review> {
        val result: ArrayList<Review> = arrayListOf()
        if(jsonObject == null) {
            return result
        }
        try {
            val jsonArray: JSONArray = jsonObject.getJSONArray(keyResults)
            for (i in 0 until jsonArray.length()) {
                val jsonObjectReview = jsonArray.getJSONObject(i)
                val author = jsonObjectReview.getString(keyAuthor)
                val content = jsonObjectReview.getString(keyContent)
                val review = Review(author, content)
                result.add(review)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    fun getTrailersFromJSON(jsonObject: JSONObject?): ArrayList<Trailer> {
        val result: ArrayList<Trailer> = arrayListOf()
        if(jsonObject == null) {
            return result
        }
        try {
            val jsonArray: JSONArray = jsonObject.getJSONArray(keyResults)
            for (i in 0 until jsonArray.length()) {
                val jsonObjectTrailers = jsonArray.getJSONObject(i)
                val key = "$baseYoutubeURL${jsonObjectTrailers.getString(keyKeyOfVideo)}"
                val nameVideo = jsonObjectTrailers.getString(keyNameVideo)
                val trailer = Trailer(key, nameVideo)
                result.add(trailer)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    fun getShowFromJSON(jsonObject: JSONObject?): ArrayList<Show> {
        val result: ArrayList<Show> = arrayListOf()

        if(jsonObject == null) {
            return result
        }

        try {
            val jsonArray: JSONArray = jsonObject.getJSONArray(keyResults)

            for (i in 0 until jsonArray.length()) {
                val objectShow = jsonArray.getJSONObject(i)

                val id: Int = objectShow.getInt(keyId)
                val voteCount: Int = objectShow.getInt(keyVoteCount)
                val name: String = objectShow.getString(keyName)
                val originalName: String = objectShow.getString(keyOriginalName)
                val overview: String = objectShow.getString(keyOverview)
                val posterPath: String = basePosterURL + smallPosterSize + objectShow.getString(keyPosterPath)
                val bigPosterPath = basePosterURL + bigPosterSize + objectShow.getString(keyPosterPath)
                val backdropPath: String = objectShow.getString(keyBackdropPath)
                val voteAverage: Double = objectShow.getDouble(keyVoteAverage)
                val firstAirDate: String = objectShow.getString(keyFirstAirDate) + " "


                val show: Show = Show(id, voteCount, name, originalName,overview, posterPath, bigPosterPath,backdropPath,voteAverage,firstAirDate)

                result.add(show)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return result
    }

    fun getMoreInfoFromJSON(jsonObject: JSONObject?): ArrayList<MoreInfo> {
        val result: ArrayList<MoreInfo> = arrayListOf()
        if(jsonObject == null) {
            return result
        }
        try {
            var status = jsonObject.getString(keyStatus)
            if(Locale.getDefault().language == "ru") {
                status = translateToRussian(status)
            }
            val dateNextEpisode: String
            val objDateNextEp = jsonObject.isNull(keyNextEpisode)
            dateNextEpisode = if(!objDateNextEp) {
                jsonObject.getJSONObject(keyNextEpisode).getString(keyDateNextEpisode)
            } else if(status == "Ended" || status == "Завершился") {
                "сериал закрыт"
            } else {
                "Дата выхода неизвестна"
            }

            val jsonArrayGenres: JSONArray = jsonObject.getJSONArray(keyGenres)
            for (i in 0 until jsonArrayGenres.length()) {
                val jsonObjectGenres = jsonArrayGenres.getJSONObject(i)
                if(i != 0 && i != jsonArrayGenres.length()) genresArray.append(", ")
                genresArray.append(jsonObjectGenres.getString(keyName))
            }

            val jsonArraySeasons: JSONArray = jsonObject.getJSONArray(keySeasons)
            val countSeasons = jsonArraySeasons.getJSONObject(jsonArraySeasons.length()-1).getString(keySeasonsNumber)

            val moreInfo = MoreInfo(status, genresArray.toString(), dateNextEpisode, countSeasons)
            result.add(moreInfo)

        } catch (e: JSONException) {
            Log.i("penguinTest", "error")
            e.printStackTrace()
        }
        return result
    }

    private fun translateToRussian(statusEng: String): String {
        return when (statusEng) {
            "Ended" -> "Завершился"
            "Returning Series" -> "Продолжается"
            "Pilot" -> "Пилот"
            "In Production" -> "В производстве"
            "Canceled" -> "Отменен"
            else -> "Планируется"
        }
    }


}