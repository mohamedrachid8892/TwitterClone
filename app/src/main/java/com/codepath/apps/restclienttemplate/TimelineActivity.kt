package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.models.TweetDao
import com.codepath.apps.restclienttemplate.models.User
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.Headers
import org.json.JSONException


class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var floatingActionButton: FloatingActionButton
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    val tweets = ArrayList<Tweet>()
    lateinit var tweetDao : TweetDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.ic_launcher)
        supportActionBar?.setTitle("")
        supportActionBar?.setDisplayUseLogoEnabled(true)

        client = TwitterApplication.getRestClient(this)

        swipeContainer = findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")
            populateHomeTimeline()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // Initialize the RecyclerView for the Timeline Activity
        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(this, tweets)

        // Initialize the EndlessRecyclerView
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)

        rvTweets.layoutManager = linearLayoutManager
        rvTweets.adapter = adapter

        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                val maxId = tweets.get(tweets.size - 1).id
                loadMoreTweets(maxId)
            }
        }

        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)

        rvTweets.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                linearLayoutManager.orientation
            )
        )

        floatingActionButton = findViewById(R.id.composeButton)
        floatingActionButton.setOnClickListener{
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        tweetDao = (applicationContext as TwitterApplication).getDatabase().tweetDao()!!
        adapter.clear()
        AsyncTask.execute { // Request list of Tweets with Users using DAO
            val tweetsFromDatabase = tweetDao.recentItems()
            Log.i(TAG, "Showing data from database")

            // TweetWithUser has to be converted Tweet objects with nested User objects (see next snippet)
            val tweetList: ArrayList<Tweet> = TweetWithUser.getTweetList(tweetsFromDatabase)
            adapter.addAll(tweetList)
        }
        populateHomeTimeline()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handles clicks on menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.compose) {
//            // Navigate to compose screen
//            val intent = Intent(this, ComposeActivity::class.java)
//            startActivityForResult(intent, REQUEST_CODE)
//        }
        return super.onOptionsItemSelected(item)
    }

    // This method is called when we come back from ComposeActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            // Get data from our intent (our tweet)
            val tweet = data?.getParcelableExtra("tweet") as Tweet

            // Update timeline
            // Modifying the data source of tweets
            tweets.add(0, tweet)

            // Update the adapter
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun loadMoreTweets(maxId: Long) {
        client.getNextPageOfTweets(object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "loadMoreTweets onSuccess!")

                val jsonArray = json.jsonArray

                try {
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    val listOfNewUsers = User.fromJsonTweetArray(listOfNewTweetsRetrieved)
                    tweets.addAll(listOfNewTweetsRetrieved)
                    AsyncTask.execute(object: Runnable {
                        override fun run() {
                            (applicationContext as TwitterApplication).getDatabase().runInTransaction(object: Runnable{
                                override fun run() {
                                    for (i in 0 until listOfNewTweetsRetrieved.size) {
                                        tweetDao.insertModel(listOfNewUsers[i])
                                        tweetDao.insertModel(listOfNewTweetsRetrieved[i])
                                    }
                                }

                            })
                        }
                    })
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception HERE $e")
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                Log.i(TAG, "onFailure in function loadMoreTweets $statusCode")
            }
        },maxId)
    }

    fun populateHomeTimeline() {
        client.getHomeTimeline(object : JsonHttpResponseHandler() {
            
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess!")

                val jsonArray = json.jsonArray

                try {
                    // Clear out currently fetched tweets
                    adapter.clear()
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    val listOfNewUsers = User.fromJsonTweetArray(listOfNewTweetsRetrieved)
                    tweets.addAll(listOfNewTweetsRetrieved)

                    AsyncTask.execute(object: Runnable {
                        override fun run() {
                            (applicationContext as TwitterApplication).getDatabase().runInTransaction(object: Runnable{
                                override fun run() {
                                    for (i in 0 until listOfNewTweetsRetrieved.size) {
                                        tweetDao.insertModel(listOfNewUsers[i])
                                        tweetDao.insertModel(listOfNewTweetsRetrieved[i])
                                    }
                                }

                            })
                        }
                    })

                    adapter.notifyDataSetChanged()
                    scrollListener?.resetState()
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception here2 $e")
                }

            }
            
            override fun onFailure(
                statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure $statusCode")
            }

        })
    }

    companion object {
        const val TAG = "TimelineActivity"
        val REQUEST_CODE = 10
    }

}