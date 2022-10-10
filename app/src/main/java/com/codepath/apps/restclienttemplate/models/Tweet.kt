package com.codepath.apps.restclienttemplate.models

import com.codepath.apps.restclienttemplate.TimeFormatter
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import kotlinx.parcelize.RawValue

@Parcelize
data class Tweet (
    var body: String= "",
    var createdAt: String = "",
    var user: @RawValue User? = null,
    var id: Long = 1,
) : Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject) : Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"))
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            tweet.id = jsonObject.getLong("id")
            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
    }
}