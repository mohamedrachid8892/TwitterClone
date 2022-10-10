package com.codepath.apps.restclienttemplate

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet

const val TWEET_EXTRA = "TWEET_EXTRA"
class TweetsAdapter(private val context: Context, val tweets: ArrayList<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate our item layout
        val view = inflater.inflate(R.layout.item_tweet, parent, false)

        return ViewHolder(view)
    }

    // Populating data into the item through holder
    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {
        // Get the data model based on the position
        // Set item views based on views and data model
        val tweet = tweets[position]
        holder.bind(tweet)
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    // Clean all elements of the recycler
    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val ivProfileImage = itemView.findViewById<ImageView>(R.id.ivProfileImage)
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweetBody = itemView.findViewById<TextView>(R.id.tvTweetBody)
        val tvCreatedAt = itemView.findViewById<TextView>(R.id.tvCreatedAt)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(tweet: Tweet) {
            tvUsername.text = tweet.user?.name
            tvTweetBody.text = tweet.body
            tvCreatedAt.text = tweet.createdAt

            Glide.with(context)
                .load(tweet.user?.publicImageUrl)
                .into(ivProfileImage)
        }
        override fun onClick(v: View?) {
            val tweet = tweets[adapterPosition]

            val intent = Intent(context, DetailTweet::class.java)
            intent.putExtra(TWEET_EXTRA, tweet)
            context.startActivity(intent)
        }
    }
}