package com.example.vmedvediev.redditapp.comments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.vmedvediev.redditapp.Account.LoginActivity
import com.example.vmedvediev.redditapp.FeedAPI
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.WebViewActivity
import com.example.vmedvediev.redditapp.XmlExtractor
import com.example.vmedvediev.redditapp.model.Comment
import com.example.vmedvediev.redditapp.model.Entry
import com.example.vmedvediev.redditapp.model.Feed
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.comments_activity_header.*
import kotlinx.android.synthetic.main.comments_in_comments_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class CommentsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CommentsActivity"
        private const val BASE_URL = "https://www.reddit.com/r/"
    }

    private lateinit var postUrl: String
    private lateinit var postThumbnailUrl: String
    private lateinit var postTitle: String
    private lateinit var postAuthor: String
    private lateinit var postUpdated: String
    private lateinit var currentFeed: String
    private val commentsList = ArrayList<Comment>()

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_comments)

         setupToolbar()
         setupImageLoader()
         initPost()
         init()
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
                val entries = response?.body()?.entrys
                prepareCommentsFromEntries(entries)
                initRecycler()
            }

            override fun onFailure(call: Call<Feed>?, t: Throwable?) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: ${t?.message}")
                Toast.makeText(this@CommentsActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun prepareCommentsFromEntries(entries: List<Entry>?) {
        entries?.forEach {
            val xmlExtractor = XmlExtractor(it.content,"<div class=\"md\"><p>","</p>")
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
    }

    private fun initRecycler() {
        commentsRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@CommentsActivity)
            adapter = CommentsRecyclerViewAdapter(this@CommentsActivity, commentsList)
        }

        commentsLoadingProgressBar?.visibility = View.GONE
        progressTextView?.visibility = View.GONE
    }

    private fun initPost() {
        intent.let {
            postUrl = it.getStringExtra(getString(R.string.post_url))
            postThumbnailUrl = it.getStringExtra(getString(R.string.post_thumbnail))
            postTitle = it.getStringExtra(getString(R.string.post_title))
            postAuthor = it.getStringExtra(getString(R.string.post_author))
            postUpdated = it.getStringExtra(getString(R.string.post_updated))
        }

        postTitleTextView?.text = postTitle
        postAuthorTextView?.text = postAuthor
        postUpdatedTextView?.text = postUpdated
        showImage(postThumbnailUrl, postThumbnailImageView, postLoadingProgressBar)

        try {
            val splittedUrl = postUrl.split(BASE_URL)
            currentFeed = splittedUrl[1]
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: ${e.message}")
        }

        postThumbnailImageView.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(getString(R.string.url), postUrl)
            startActivity(intent)
        }
    }

    private fun showImage(imageUrl: String, imageView: ImageView, progressBar: ProgressBar) {
        val imageLoader = ImageLoader.getInstance()
        val defaultImage = this.resources.getIdentifier("@drawable/reddit_alien", null, this.packageName)
        val options = DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build()

        imageLoader.displayImage(imageUrl, imageView, options, object : ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                progressBar?.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                progressBar?.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                progressBar?.visibility = View.GONE
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                progressBar?.visibility = View.GONE
            }
        })
    }

    private fun setupImageLoader() {
        val defaultOptions = DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(FadeInBitmapDisplayer(300)).build()

        val config = ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build()

        ImageLoader.getInstance().init(config)
    }

    private fun initRetrofit() : FeedAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        return retrofit.create(FeedAPI::class.java)
    }
}
