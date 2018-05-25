package com.example.vmedvediev.redditapp

import com.example.vmedvediev.redditapp.model.Feed
import retrofit2.Call
import retrofit2.http.GET

interface FeedAPI {

    @GET("pics/.rss")
    fun getFeed(): Call<Feed>
}