package com.example.vmedvediev.redditapp

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.vmedvediev.redditapp.model.Post
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import org.jetbrains.anko.find

class PostsRecyclerViewAdapter(val context: Context, val postsList: ArrayList<Post>) : RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "PostsRecyclerViewAdapter"
    }

    init {
        setupImageLoader()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = postsList[position].title
        holder.author.text = postsList[position].author
        holder.dateUpdated.text = postsList[position].dateUpdated

        val imageLoader = ImageLoader.getInstance()
        val defaultImage = context.resources.getIdentifier("@drawable/reddir_alien", null, context.packageName)
        val options = DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build()

        imageLoader.displayImage(postsList[position].thumnailUrl, holder.thumbnailUrl, options, object : ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                holder.progressBar.visibility = View.GONE
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                holder.progressBar.visibility = View.VISIBLE
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
                holder.progressBar.visibility = View.GONE
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                holder.progressBar.visibility = View.GONE
            }

        })
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView
        val author: TextView
        val dateUpdated: TextView
        val progressBar: ProgressBar
        val thumbnailUrl: ImageView

        init {
            title = view.find(R.id.cardTitle)
            author = view.find(R.id.cardAuthor)
            dateUpdated = view.find(R.id.cardUpdated)
            progressBar = view.find(R.id.cardProgressBar)
            thumbnailUrl = view.find(R.id.cardImage)
        }
    }

    private fun setupImageLoader() {
        val defaultOptions = DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(FadeInBitmapDisplayer(300)).build()

        val config = ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build()

        ImageLoader.getInstance().init(config)
    }
}