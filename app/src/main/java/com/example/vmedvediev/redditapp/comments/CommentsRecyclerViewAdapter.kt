package com.example.vmedvediev.redditapp.comments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.comments.CommentsActivity
import com.example.vmedvediev.redditapp.inflate
import com.example.vmedvediev.redditapp.model.Comment
import com.example.vmedvediev.redditapp.model.Post
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_post.view.*

class CommentsRecyclerViewAdapter(val context: Context, val commentsList: ArrayList<Comment>) : RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = commentsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            title.text = commentsList[position].comment
            author.text = commentsList[position].author
            dateUpdated.text = commentsList[position].updated
            progressBar.visibility = View.GONE
        }
    }

    inner class ViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(parent?.inflate(R.layout.item_comment)) {

        val title: TextView = itemView.commentTextView
        val author: TextView = itemView.commentAuthorTextView
        val dateUpdated: TextView = itemView.commentUpdatedTextView
        val progressBar: ProgressBar = itemView.commentProgressBar
    }
}