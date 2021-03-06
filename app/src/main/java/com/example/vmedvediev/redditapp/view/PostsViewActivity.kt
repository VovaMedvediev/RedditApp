package com.example.vmedvediev.redditapp.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.example.vmedvediev.redditapp.NavigationManager
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.model.Post
import com.example.vmedvediev.redditapp.presenter.PostsPresenter
import com.example.vmedvediev.redditapp.view.adapters.PostsRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_activity_part.*

class PostsViewActivity : AppCompatActivity(), PostsPresenter.View {

    private val presenter: PostsPresenter by lazy {
        PostsPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        showPosts(ArrayList())

        refreshPostsButton.setOnClickListener {
            presenter.getPosts(feedNameEditText.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(mainToolBar)
        mainToolBar.setOnMenuItemClickListener {
            startActivity(NavigationManager.changeScreen(this, it.itemId))
            false
        }
    }

    override fun showError() {
        Toast.makeText(this, "Error occurred! Input correct feed name.", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        loadingProgressBar?.visibility = VISIBLE
    }

    override fun hideLoading() {
        loadingProgressBar?.visibility = GONE
    }

    override fun showPosts(posts: ArrayList<Post>) {
            postsRecyclerView?.apply {
                layoutManager = LinearLayoutManager(this@PostsViewActivity)
                adapter = PostsRecyclerViewAdapter(context, posts, { post: Post -> onPostClicked(post) })
            }
    }

    private fun onPostClicked(post: Post) {
        val intent = Intent(this, CommentsViewActivity::class.java).apply {
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