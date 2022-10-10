package com.codepath.apps.restclienttemplate

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet

class DetailTweet : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvBody: TextView
    private lateinit var ivProfileImage: ImageView
    private lateinit var tvCreatedAt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        tvUsername = findViewById(R.id.tvUsernameDetail)
        tvBody = findViewById(R.id.tvTweetBodyDetail)
        tvCreatedAt = findViewById(R.id.tvCreatedAtDetail)
        ivProfileImage = findViewById(R.id.ivProfileImageDetail)

        val tweet = intent.getParcelableExtra<Tweet>(TWEET_EXTRA) as Tweet
        tvUsername.text = tweet.user?.name
        tvBody.text = tweet.body
        tvCreatedAt.text = tweet.createdAt

        Glide.with(this)
            .load(tweet.user?.publicImageUrl)
            .into(ivProfileImage)
    }
}