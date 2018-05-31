package com.example.vmedvediev.redditapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import com.example.vmedvediev.redditapp.Account.LoginActivity
import com.example.vmedvediev.redditapp.R.string.post_url
import com.example.vmedvediev.redditapp.comments.CommentsActivity
import com.example.vmedvediev.redditapp.model.Feed
import com.example.vmedvediev.redditapp.model.Post
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_activity_part.*
import kotlinx.android.synthetic.main.main_activity_part.refreshPostsButton
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

        setupToolbar()

        refreshPostsButton.setOnClickListener {
            val feedName = feedNameEditText.text.toString()
            if (TextUtils.isEmpty(feedName)) {
                Toast.makeText(this@MainActivity, "Input a feed name!", Toast.LENGTH_SHORT).show()
            } else {
                currentFeed = feedName
                init()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(mainToolBar)

        mainToolBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.navigationLogin -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            false
        }
    }

    private fun init() {
        val call = initRetrofit().getFeed(currentFeed)

        call.enqueue(object : Callback<Feed> {

            override fun onResponse(call: Call<Feed>?, response: Response<Feed>?) {
                val entrys = response?.body()?.entrys
                val posts = ArrayList<Post>()

                entrys?.forEach { entry ->
                    val extractXml1 = XmlExtractor(entry.content, "<a href=")
                    val postContent = extractXml1.parseHtml()

                    val extractXml2 = XmlExtractor(entry.content, "<img src=")

                    try {
                        postContent.add(extractXml2.parseHtml()[0])
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
                            postContent[lastPosition],
                            entry.id
                    ))
                }

                postsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = PostsRecyclerViewAdapter(context, posts, {post: Post -> onPostClicked(post)})
                }
            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: ${t?.message}")
                Toast.makeText(this@MainActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initRetrofit() : FeedAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        return retrofit.create(FeedAPI::class.java)
    }

    private fun onPostClicked(post: Post) {
        val intent = Intent(this, CommentsActivity::class.java).apply {
            putExtra(getString(R.string.post_url), post.postUrl)
            putExtra(getString(R.string.post_thumbnail), post.thumnailUrl)
            putExtra(getString(R.string.post_title), post.title)
            putExtra(getString(R.string.post_author), post.author)
            putExtra(getString(R.string.post_updated), post.dateUpdated)
            putExtra(getString(R.string.post_id), post.id)
        }
        startActivity(intent)
    }
}