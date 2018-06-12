package com.example.vmedvediev.redditapp.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.inflate
import com.example.vmedvediev.redditapp.model.Comment
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentsRecyclerViewAdapter(private val commentsList: ArrayList<Comment>,
                                  private val onCommentClickListener: (Comment) -> Unit) : RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = commentsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            title?.text = commentsList[position].comment
            author?.text = commentsList[position].author
            dateUpdated?.text = commentsList[position].updated
            progressBar?.visibility = View.GONE

            holder.itemView.setOnClickListener {
                onCommentClickListener(commentsList[position])
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(parent?.inflate(R.layout.item_comment)) {

        val title: TextView = itemView.commentTextView
        val author: TextView = itemView.commentAuthorTextView
        val dateUpdated: TextView = itemView.commentUpdatedTextView
        val progressBar: ProgressBar = itemView.commentProgressBar
    }
}