package com.example.vmedvediev.redditapp.presenter

import android.util.Log
import com.example.vmedvediev.redditapp.CoroutineContextProvider
import com.example.vmedvediev.redditapp.model.Feed
import com.example.vmedvediev.redditapp.model.XmlExtractor
import com.example.vmedvediev.redditapp.model.NetworkManager
import com.example.vmedvediev.redditapp.model.Post
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class PostsPresenter(private val view: View, private val contextPool: CoroutineContextProvider = CoroutineContextProvider()) {

    companion object {
        private const val TAG = "PostsPresenter"
    }

    fun getPosts(postName: String) : ArrayList<Post> {
        val posts = ArrayList<Post>()
        launch(contextPool.Main) {
            try {
                view.showLoading()

                val data = withContext(contextPool.IO) {
                    val feed = NetworkManager
                            .initRetrofit(SimpleXmlConverterFactory.create()).getFeed(postName).execute().body()
                    return@withContext preparePostsFromFeed(feed, posts)
                }
                view.showPosts(data)
                view.hideLoading()
            } catch (e: Exception) {
                view.hideLoading()
                view.showError()
            }
        }
        return posts
    }

    private fun preparePostsFromFeed(feed: Feed?, posts: ArrayList<Post>) : ArrayList<Post> {
        feed?.entrys?.forEach { entry ->
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
        return posts
    }


    interface View {

        fun showLoading()

        fun hideLoading()

        fun showPosts(posts: ArrayList<Post>)

        fun showError()
    }
}