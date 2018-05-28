package com.example.vmedvediev.redditapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.vmedvediev.redditapp.model.Feed
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://www.reddit.com/r/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        val feedApi = retrofit.create(FeedAPI::class.java)

        val call = feedApi.getFeed()

        call.enqueue(object : Callback<Feed> {

            override fun onResponse(call: Call<Feed>?, response: Response<Feed>?) {
                Log.d(TAG, "onResponse: feed: " + response?.body()?.entrys)
                Log.d(TAG, "onResponse: Server Response: " + response.toString())

                val entrys = response?.body()?.entrys
                Log.d(TAG, "onResponse: entrys: $entrys")
//                Log.d(TAG, "onResponse: author: ${entrys?.get(0)?.author}")
//                Log.d(TAG, "onResponse: updated: ${entrys?.get(0)?.updated}")
//                Log.d(TAG, "onResponse: title: ${entrys?.get(0)?.title}")

                for (item in entrys!!) {
                    val extraclXml = ExtractXML("<a href=", entrys[0].content)
                    val postContent: MutableList<String> = extraclXml.start()

                    val extractXml2 = ExtractXML("<img src=", entrys[0].content)

                    try {
                        postContent.add(extractXml2.start()[0])
                    } catch (e: NullPointerException) {
                        postContent.add("")
                        Log.e(TAG, "onResponse: NullPointerExceotion(thumbnail): ${e.message}")
                    } catch (e: IndexOutOfBoundsException) {
                        postContent.add("")
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException(thumbnail): ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: ${t?.message}")
                Toast.makeText(this@MainActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
