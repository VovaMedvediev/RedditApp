package com.example.vmedvediev.redditapp.comments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.example.vmedvediev.redditapp.ImageLoaderManager.setupImageLoader
import com.example.vmedvediev.redditapp.ImageLoaderManager.showImage
import com.example.vmedvediev.redditapp.model.NetworkManager.BASE_URL
import com.example.vmedvediev.redditapp.model.NetworkManager.initRetrofit
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.view.WebViewActivity
import com.example.vmedvediev.redditapp.model.XmlExtractor
import com.example.vmedvediev.redditapp.model.Comment
import com.example.vmedvediev.redditapp.model.Entry
import com.example.vmedvediev.redditapp.view.adapters.CommentsRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.comment_input_layout.*
import kotlinx.android.synthetic.main.comments_activity_header.*
import kotlinx.android.synthetic.main.comments_in_comments_activity.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.converter.gson.GsonConverterFactory

class CommentsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CommentsActivity"
    }

    private lateinit var postId: String
    private lateinit var postUrl: String
    private lateinit var currentFeed: String
    private val commentsList = ArrayList<Comment>()
    private lateinit var modhash: String
    private lateinit var cookie: String
    private lateinit var username: String

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_comments)

         setupToolbar()
         getSessionParams()
         setupImageLoader(this)
         initPost()
         prepareCurrentFeed()
         makeGetFeedRequest()
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
            when(it.itemId) {
                R.id.navigationLogin -> {
                    //val intent = Intent(this, LoginActivity::class.java)
                    //startActivity(intent)
                }
            }
            false
        }
    }

    private fun makeGetFeedRequest() = launch(UI) {
//            try {
//                val entries = initRetrofit(SimpleXmlConverterFactory.create()).getFeed(currentFeed).await().entrys
//                prepareCommentsFromEntries(entries)
//                initRecycler()
//            } catch (e: Exception) {
//                Log.e(TAG, "onFailure: Unable to retrieve RSS: ${e.message}")
//                Toast.makeText(this@CommentsActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
//            }
        }

    private fun prepareCommentsFromEntries(entries: List<Entry>?) = entries?.forEach {
            val xmlExtractor = XmlExtractor(it.content, "<div class=\"md\"><p>", "</p>")
            val commentDetails = xmlExtractor.parseHtml()

            try {
                commentsList.add(Comment(
                        commentDetails[0],
                        it.author?.name,
                        it.updated,
                        it.id
                ))
            } catch (e: IndexOutOfBoundsException) {
                commentsList.add(Comment(
                        "Error reading comment",
                        "None",
                        "None",
                        "None"
                ))
            }
        }

    private fun initRecycler() {
        commentsRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@CommentsActivity)
            adapter = CommentsRecyclerViewAdapter(commentsList, { comment: Comment -> onCommentClicked(comment) })
        }

        commentsLoadingProgressBar?.visibility = View.GONE
        progressTextView?.visibility = View.GONE
    }

    private fun initPost() {
        intent.let {
            postUrl = it.getStringExtra(getString(R.string.post_url))
            postId = it.getStringExtra(getString(R.string.post_id))
            postTitleTextView?.text = it.getStringExtra(getString(R.string.post_title))
            postAuthorTextView?.text = it.getStringExtra(getString(R.string.post_author))
            postUpdatedTextView?.text = it.getStringExtra(getString(R.string.post_updated))
            showImage(this, it.getStringExtra(getString(R.string.post_thumbnail)), postThumbnailImageView, postLoadingProgressBar)
        }
    }

    private fun postReply() {
        postReplyButton.setOnClickListener {
            getUserComment()
        }
    }

    private fun openPostInWebview() {
        postThumbnailImageView.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(getString(R.string.url), postUrl)
            startActivity(intent)
        }
    }

    private fun prepareCurrentFeed() {
        try {
            val splittedUrl = postUrl.split((BASE_URL + "r/").toRegex())
            currentFeed = splittedUrl[1]
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: ${e.message}")
        }
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
                makePostCommentRequest(comment)
            }
        }
    }

    private fun makePostCommentRequest(comment: String) {
            val headerMap = HashMap<String, String>()
            headerMap["User-Agent"] = username
            headerMap["X-Modhash"] = modhash
            headerMap["cookie"] = "reddit_session=$cookie"

        launch(UI) {
            try {
                val isResponseSuccessful = initRetrofit(GsonConverterFactory.create())
                        .submitComment(headerMap, "comment", postId, comment).await().success.toBoolean()
                return@launch if (isResponseSuccessful) handleSuccessPostComment() else handleUnsuccessPostComment()
            } catch (e: Exception) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: ${e.message}")
                Toast.makeText(this@CommentsActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSuccessPostComment() {
        Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
    }

    private fun handleUnsuccessPostComment() {
        Toast.makeText(this, "Error occured! Dud you sign in?", Toast.LENGTH_SHORT).show()
    }

    private fun onCommentClicked(comment: Comment) {
        getUserComment()
    }

     private fun getSessionParams() {
         val preferences = PreferenceManager.getDefaultSharedPreferences(this)
         preferences.let {
             username = it.getString(getString(R.string.SessionUsername), "")
             modhash = it.getString(getString(R.string.SessionModhash), "")
             cookie = it.getString(getString(R.string.SessionCookie), "")
         }
     }
}



