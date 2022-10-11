package com.codepath.apps.restclienttemplate.models

import com.codepath.apps.restclienttemplate.TimeFormatter
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(foreignKeys = arrayOf(ForeignKey(entity = User::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("userId")
)))
data class Tweet (
    @ColumnInfo
    var body: String= "",

    @ColumnInfo
    var createdAt: String = "",

    @ColumnInfo
    var userId: Long = 1,

    @Ignore
    var user: @RawValue User? = null,

    @PrimaryKey
    @ColumnInfo
    var id: Long = 1,
) : Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject) : Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"))
            tweet.id = jsonObject.getLong("id")

            val user = User.fromJson(jsonObject.getJSONObject("user"))
            tweet.user = user
            tweet.userId = user.id

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