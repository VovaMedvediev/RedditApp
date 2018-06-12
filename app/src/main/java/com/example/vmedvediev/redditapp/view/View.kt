package com.example.vmedvediev.redditapp.view

import com.example.vmedvediev.redditapp.model.Post

interface View {

    fun showLoading()

    fun hideLoading()

    fun showPosts(posts: ArrayList<Post>)

    fun showError()
}