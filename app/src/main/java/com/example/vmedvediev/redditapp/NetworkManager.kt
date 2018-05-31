package com.example.vmedvediev.redditapp

import com.example.vmedvediev.redditapp.comments.CommentsActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object NetworkManager {

    const val BASE_URL = "https://www.reddit.com/"
    const val API_TYPE = "json"

    fun initGsonRetrofit() : FeedAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(FeedAPI::class.java)
    }

    fun initXmlRetrofit() : FeedAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        return retrofit.create(FeedAPI::class.java)
    }
}