package com.example.vmedvediev.redditapp

import com.example.vmedvediev.redditapp.model.CommentChecker
import com.example.vmedvediev.redditapp.model.Feed
import com.example.vmedvediev.redditapp.model.LoginChecker
import retrofit2.Call
import retrofit2.http.*

interface FeedAPI {
    
    @GET("r/{feedName}/.rss")
    fun getFeed(@Path("feedName") feedName: String) : Call<Feed>

    @POST("api/login/{user}")
    @Headers("Content-Type: application/json")
    fun signIn(
               @Path("user") username: String,
               @Query("user") user: String,
               @Query("passwd") password: String,
               @Query("api_type") type: String) : Call<LoginChecker>

    @POST("api/{comment}")
    fun submitComment(@HeaderMap headers: Map<String, String>,
               @Path("comment") comment: String,
               @Query("parent") parent: String,
               @Query("amp;text") text: String) : Call<CommentChecker>

}