package com.example.vmedvediev.redditapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.example.vmedvediev.redditapp.Account.LoginActivity
import com.example.vmedvediev.redditapp.NetworkManager.initRetrofit
import com.example.vmedvediev.redditapp.comments.CommentsActivity
import com.example.vmedvediev.redditapp.model.Post
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_activity_part.*
import kotlinx.android.synthetic.main.main_activity_part.refreshPostsButton
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
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

    private fun init() = launch(UI) {
            try {
                val entries = initRetrofit(SimpleXmlConverterFactory.create()).getFeed(currentFeed).await().entrys
                val posts = ArrayList<Post>()

                entries?.forEach { entry ->
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

                updateUi(posts)

            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
                Toast.makeText(this@MainActivity, "Error occured! Input correct feed name.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun updateUi(posts: ArrayList<Post>) {
        postsRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PostsRecyclerViewAdapter(context, posts, {post: Post -> onPostClicked(post)})
        }
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