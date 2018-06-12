package com.example.vmedvediev.redditapp.presenter

import android.util.Log
import com.example.vmedvediev.redditapp.model.XmlExtractor
import com.example.vmedvediev.redditapp.model.NetworkManager
import com.example.vmedvediev.redditapp.model.Post
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class PostsPresenter(private val view: View) {

    companion object {
        private const val TAG = "PostsPresenter"
    }

    fun getPosts(postName: String) = launch(UI) {
            try {
                view.showLoading()

                val data = bg {
                    val feed = NetworkManager
                            .initRetrofit(SimpleXmlConverterFactory.create()).getFeed(postName).execute().body()
                    val posts = ArrayList<Post>()

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
                    return@bg posts
                }

                view.showPosts(data.await())
                view.hideLoading()

            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
                view.hideLoading()
                view.showError()
            }
        }


    interface View {

        fun showLoading()

        fun hideLoading()

        fun showPosts(posts: ArrayList<Post>)

        fun showError()
    }
}