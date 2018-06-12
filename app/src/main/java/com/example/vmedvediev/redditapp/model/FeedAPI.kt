package com.example.vmedvediev.redditapp.model

import kotlinx.coroutines.experimental.Deferred
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
               @Query("api_type") type: String) : Deferred<LoginChecker>

    @POST("api/{comment}")
    fun submitComment(@HeaderMap headers: Map<String, String>,
               @Path("comment") comment: String,
               @Query("parent") parent: String,
               @Query("amp;text") text: String) : Deferred<CommentChecker>

}