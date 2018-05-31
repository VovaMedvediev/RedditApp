package com.example.vmedvediev.redditapp

import com.example.vmedvediev.redditapp.model.CommentChecker
import com.example.vmedvediev.redditapp.model.Feed
import com.example.vmedvediev.redditapp.model.LoginChecker
import retrofit2.Call
import retrofit2.http.*

interface FeedAPI {
    
    @GET("{feedName}/.rss")
    fun getFeed(@Path("feedName") feedName: String) : Call<Feed>

    @POST("{user}")
    fun signIn(@HeaderMap headers: Map<String, String>,
               @Path("user") username: String,
               @Query("user") user: String,
               @Query("passwd") password: String,
               @Query("api_type") type: String) : Call<LoginChecker>

    @POST("{comment}")
    fun submitComment(@HeaderMap headers: Map<String, String>,
               @Path("comment") comment: String,
               @Query("parent") parent: String,
               @Query("amp;text") text: String) : Call<CommentChecker>

}