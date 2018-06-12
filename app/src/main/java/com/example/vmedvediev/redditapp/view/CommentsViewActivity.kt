package com.example.vmedvediev.redditapp.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.example.vmedvediev.redditapp.ImageLoaderManager
import com.example.vmedvediev.redditapp.NavigationManager
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.model.Comment
import com.example.vmedvediev.redditapp.presenter.CommentsPresenter
import com.example.vmedvediev.redditapp.view.adapters.CommentsRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.comment_input_layout.*
import kotlinx.android.synthetic.main.comments_activity_header.*
import kotlinx.android.synthetic.main.comments_in_comments_activity.*

class CommentsViewActivity : AppCompatActivity(), CommentsPresenter.View {

    private lateinit var presenter: CommentsPresenter
    private lateinit var postUrl: String
    private lateinit var postId: String
    private lateinit var modhash: String
    private lateinit var cookie: String
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        setupToolbar()
        getSessionParams()
        ImageLoaderManager.setupImageLoader(this)
        initPost()
        initPresenter()
        presenter.makeGetFeedRequest(postUrl)

        postReply()
        openPostInWebview()
    }

    override fun onPostResume() {
        super.onPostResume()
        getSessionParams()
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

    private fun initPresenter() {
        presenter = CommentsPresenter(this)
    }

    private fun getSessionParams() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.let {
            username = it.getString(getString(R.string.SessionUsername), "")
            modhash = it.getString(getString(R.string.SessionModhash), "")
            cookie = it.getString(getString(R.string.SessionCookie), "")
        }
    }

    private fun initPost() {
        intent.let {
            postUrl = it.getStringExtra(getString(R.string.post_url))
            postId = it.getStringExtra(getString(R.string.post_id))
            postTitleTextView?.text = it.getStringExtra(getString(R.string.post_title))
            postAuthorTextView?.text = it.getStringExtra(getString(R.string.post_author))
            postUpdatedTextView?.text = it.getStringExtra(getString(R.string.post_updated))
            ImageLoaderManager.showImage(this, it.getStringExtra(getString(R.string.post_thumbnail)), postThumbnailImageView, postLoadingProgressBar)
        }
    }

    private fun openPostInWebview() {
        postThumbnailImageView.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(getString(R.string.url), postUrl)
            startActivity(intent)
        }
    }


    private fun postReply() {
        postReplyButton.setOnClickListener {
            getUserComment()
        }
    }

    private fun onCommentClicked(comment: Comment) {
        getUserComment()
    }

    private fun getUserComment() {
        val dialog = Dialog(this)

        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt()

        dialog.apply {
            title = "Dialog"
            setContentView(R.layout.comment_input_layout)
            window.setLayout(width, height)
            show()

            postCommentButton.setOnClickListener {
                val comment = commentEditText?.text.toString()
                presenter.makePostCommentRequest(comment, username, modhash, cookie, postId)
            }
        }
    }

    override fun showLoadingComments() {
        commentsLoadingProgressBar?.visibility = VISIBLE
        progressTextView?.visibility = VISIBLE
    }

    override fun hideLoadingComments() {
        commentsLoadingProgressBar?.visibility = GONE
        progressTextView?.visibility = GONE
    }

    override fun showComments(comments: ArrayList<Comment>) {
        commentsRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@CommentsViewActivity)
            adapter = CommentsRecyclerViewAdapter(comments, { comment: Comment -> onCommentClicked(comment) })
        }
    }

    override fun showCommentsError() {
        Toast.makeText(this, "An Error Occurred!", Toast.LENGTH_SHORT).show()
    }

    override fun handleSuccessPostComment() {
        Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
    }

    override fun handleUnsuccessPostComment() {
        Toast.makeText(this, "Error occured! Dud you sign in?", Toast.LENGTH_SHORT).show()
    }

    override fun showPostCommentError() {
        Toast.makeText(this, "An Error Occurred!", Toast.LENGTH_SHORT).show()
    }
}