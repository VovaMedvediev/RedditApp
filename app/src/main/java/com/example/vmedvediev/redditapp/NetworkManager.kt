package com.example.vmedvediev.redditapp

import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object NetworkManager {

    const val BASE_URL = "https://www.reddit.com/"
    const val API_TYPE = "json"

    fun initRetrofit(factory: Converter.Factory) : FeedAPI {
        val converterFactory = if (factory is GsonConverterFactory) GsonConverterFactory.create() else SimpleXmlConverterFactory.create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(converterFactory)
                .build()
        return retrofit.create(FeedAPI::class.java)
    }
}