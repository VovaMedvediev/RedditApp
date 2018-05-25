package com.example.vmedvediev.redditapp

import com.example.vmedvediev.redditapp.model.Feed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FeedAPI {
    
    @GET("{feedName}/.rss")
    fun getFeed(@Path("feedName") feedName: String): Call<Feed>

}