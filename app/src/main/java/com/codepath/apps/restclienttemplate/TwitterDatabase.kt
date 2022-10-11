package com.codepath.apps.restclienttemplate

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.models.TweetDao
import com.codepath.apps.restclienttemplate.models.User


@Database(entities = [Tweet::class, User::class], version = 1)
abstract class TwitterDatabase : RoomDatabase() {
    abstract fun tweetDao(): TweetDao?

    companion object {
        // Database name to be used
        const val NAME = "TwitterDataBase"
    }
}