package com.impudentpenguin.tvshows.utils

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.content.AsyncTaskLoader
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ExecutionException

class NetworkUtils {

    /** URL **/
    private val apiKey = "60f71c488150ae1cf097c94a1ae23891"
    private val baseURL = "https://api.themoviedb.org/3/discover/tv"
    private val baseURLVideos = "https://api.themoviedb.org/3/tv/%s/videos" // %s - showId
    private val baseURLReviews = "https://api.themoviedb.org/3/tv/%s/reviews" // %s - showId
    private val baseURLMoreInfo = "https://api.themoviedb.org/3/tv/%s" // %s - showId

    /** Params **/
    private val paramsApiKey ="api_key"
    private val paramsLanguage = "language"
    private val paramsSortBy = "sort_by"
    private val paramsPage  = "page"
    private val paramsMinVoteCount = "vote_count.gte"

    /** Values **/
    private val sortByPopularity = "popularity.desc"
    private val sortByTopRated = "vote_average.desc"
    private val minVoteCountValue = "320"
    val popularity: Int = 0
    val topRated: Int = 1

    class JSONLoader(context: Context, private val bundle: Bundle?) : AsyncTaskLoader<JSONObject>(context) {

        var onStartLoadingListener: OnStartLoadingListener? = null


        interface OnStartLoadingListener {
            fun onStartLoading()
        }

        override fun onStartLoading() {
            super.onStartLoading()
            if(onStartLoadingListener != null) {
                onStartLoadingListener!!.onStartLoading()
            }
            forceLoad()
        }

        override fun loadInBackground(): JSONObject? {
            if (bundle == null) {
                return null
            }
            val urlAsString = bundle.getString("url")
            var url: URL? = null

            try {
                url = URL(urlAsString)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

            var result: JSONObject? = null
            var connection: HttpURLConnection? = null

            if (url == null) return null

            try {
                connection = url.openConnection() as HttpURLConnection

                val inputStreamReader = InputStreamReader(connection.inputStream)
                val reader = BufferedReader(inputStreamReader)

                val builder = StringBuilder()
                var line: String? = reader.readLine()

                while (line != null) {
                    builder.append(line)
                    line = reader.readLine()
                }

                result = JSONObject(builder.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()

            }

            return result
        }
    }

    private fun buildURLToVideos(id: Int, languageValue: String): URL? {
        val uri = Uri.parse(String.format(baseURLVideos, id)).buildUpon()
            .appendQueryParameter(paramsApiKey, apiKey)
            .appendQueryParameter(paramsLanguage, languageValue).build()
        try {
            return URL(uri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun buildURLToReviews(id: Int): URL? {
        val uri = Uri.parse(String.format(baseURLReviews, id)).buildUpon()
            .appendQueryParameter(paramsApiKey, apiKey).build()
        try {
            return URL(uri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    fun buildURL(sortBy: Int, page: Int, languageValue: String): URL? {
        var result: URL? = null
        val methodOfSort: String = if(sortBy == popularity) {
            sortByPopularity
        } else {
            sortByTopRated
        }

        val uri = Uri.parse(baseURL).buildUpon()
            .appendQueryParameter(paramsApiKey, apiKey)
            .appendQueryParameter(paramsLanguage, languageValue)
            .appendQueryParameter(paramsSortBy, methodOfSort)
            .appendQueryParameter(paramsMinVoteCount, minVoteCountValue)
            .appendQueryParameter(paramsPage, page.toString())
            .build()

        try {
            result = URL(uri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return result
    }

    private fun buildURLToMoreInfo(id: Int, languageValue: String): URL? {
        val uri = Uri.parse(String.format(baseURLMoreInfo, id)).buildUpon()
            .appendQueryParameter(paramsApiKey, apiKey)
            .appendQueryParameter(paramsLanguage, languageValue).build()
        try {
            return URL(uri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    fun getJSONForVideos (id: Int, languageValue: String): JSONObject? {
        var result: JSONObject? = null
        val url = buildURLToVideos(id, languageValue)
        try {
            result = JSONLoadTask().execute(url).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return result
    }

    fun getJSONForReviews (id: Int): JSONObject? {
        var result: JSONObject? = null
        val url = buildURLToReviews(id)
        try {
            result = JSONLoadTask().execute(url).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return result
    }

    fun getJSONForMoreInfo(id: Int, languageValue: String): JSONObject? {
        var result: JSONObject? = null
        val url = buildURLToMoreInfo(id, languageValue)
        try {
            result = JSONLoadTask().execute(url).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return result
    }

    private class JSONLoadTask: AsyncTask<URL, Unit, JSONObject>() {

        override fun doInBackground(vararg urls: URL): JSONObject? {
            var result: JSONObject? = null
            var connection: HttpURLConnection? = null

            if (urls.isEmpty()) {
                return result
            }

            try {
                connection = urls[0].openConnection() as HttpURLConnection

                val inputStreamReader = InputStreamReader(connection.inputStream)
                val reader = BufferedReader(inputStreamReader)

                val builder = StringBuilder()
                var line: String? = reader.readLine()

                while (line != null) {
                    builder.append(line)
                    line = reader.readLine()
                }

                result = JSONObject(builder.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()

            }

            return result
        }

    }
}