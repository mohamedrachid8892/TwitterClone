package com.codepath.apps.restclienttemplate

import androidx.room.Embedded
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.models.User


class TweetWithUser {
    // @Embedded notation flattens the properties of the User object into the object, preserving encapsulation.

    @Embedded
    lateinit var user : User

    // Prefix is needed to resolve ambiguity between fields: user.id and tweet.id, user.createdAt and tweet.createdAt
    @Embedded(prefix = "tweet_")
    lateinit var tweet: Tweet

    companion object {
        fun getTweetList(tweetWithUserList: List<TweetWithUser>): ArrayList<Tweet> {
            val tweets: ArrayList<Tweet> = ArrayList()
            for (i in tweetWithUserList.indices) {
                val tweetWithUser = tweetWithUserList[i]
                val tweet = tweetWithUser.tweet
                tweet.user = tweetWithUser.user
                tweets.add(tweet)
            }
            return tweets
        }
    }
}