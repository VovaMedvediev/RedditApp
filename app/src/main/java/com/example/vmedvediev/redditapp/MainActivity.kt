package com.example.vmedvediev.redditapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.vmedvediev.redditapp.model.Feed
import com.example.vmedvediev.redditapp.model.Post
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_activity_part.*
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

    private lateinit var currentFeed: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        refreshPostsButton.setOnClickListener {
            val feedName = feedNameEditText.text.toString()
            if (feedName == "") {
                Toast.makeText(this@MainActivity, "Input a feed name!", Toast.LENGTH_SHORT).show()
            } else {
                currentFeed = feedName
                init()
            }
        }
    }

    private fun init() {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        val feedApi = retrofit.create(FeedAPI::class.java)

        val call = feedApi.getFeed()

        call.enqueue(object : Callback<Feed> {

            override fun onResponse(call: Call<Feed>?, response: Response<Feed>?) {

//                Log.d(TAG, "onResponse: feed: " + response?.body()?.toString())
                Log.d(TAG, "onResponse: Server Response: " + response.toString())

                val entrys = response?.body()?.entrys

//                Log.d(TAG, "onResponse: author: ${entrys?.get(1)?.author?.name}")
//                Log.d(TAG, "onResponse: updated: ${entrys?.get(1)?.updated}")
//                Log.d(TAG, "onResponse: title: ${entrys?.get(1)?.title}")

                val posts = ArrayList<Post>()
                for (entry in entrys!!) {
                    val extractXml1 = ExtractXML(entry.content, "<a href=")
                    val postContent = extractXml1.parseHtml()

                    val extractXml2 = ExtractXML(entry.content, "<img src=")

                    try {
                        postContent.add(extractXml2.parseHtml()[0])
                    } catch (e: NullPointerException) {
                        postContent.add("NULL")
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail): ${e.message}")
                    } catch (e: IndexOutOfBoundsException) {
                        postContent.add("NULL")
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException(thumbnail): ${e.message}")
                    }

                    val lastPosition = postContent.size - 1
                    posts.add(Post(
                            entry.title,
                            entry.author?.name,
                            entry.updated,
                            postContent[0],
                            postContent[lastPosition]
                    ))
                }

//                for (post in posts) {
//                    Log.d(TAG, "onResponse: \n" +
//                            "PostUrl: ${post.postUrl} \n" +
//                            "ThumbnailUrl: ${post.thumnailUrl} \n" +
//                             "Title: ${post.title} \n" +
//                              "Author: ${post.author} \n" +
//                               "Updated: ${post.dateUpdated} \n")
//                }

                postsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = PostsRecyclerViewAdapter(context, posts)
                }
            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: ${t?.message}")
                Toast.makeText(this@MainActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
