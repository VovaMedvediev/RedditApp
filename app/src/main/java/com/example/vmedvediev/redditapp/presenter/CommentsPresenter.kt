package com.example.vmedvediev.redditapp.presenter

import android.util.Log
import android.widget.Toast
import com.example.vmedvediev.redditapp.comments.CommentsActivity
import com.example.vmedvediev.redditapp.model.*
import com.example.vmedvediev.redditapp.model.NetworkManager.initRetrofit
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class CommentsPresenter(private val view: CommentsPresenter.View) {

    companion object {
        private const val TAG = "CommentsPresenter"
    }

    private val commentsList = ArrayList<Comment>()

    fun makeGetFeedRequest(postUrl: String) = launch(UI) {
        try {
            view.showLoadingComments()
            val data = bg {
                val entries = initRetrofit(SimpleXmlConverterFactory.create())
                        .getFeed(prepareCurrentFeed(postUrl)).execute().body()?.entrys
                prepareCommentsFromEntries(entries)
                return@bg commentsList
            }
            view.showComments(data.await())
        } catch (e: Exception) {
            Log.e(TAG, "onFailure: Unable to retrieve RSS: ${e.message}")
            view.hideLoadingComments()
            view.showCommentsError()
        }
    }

    private fun prepareCurrentFeed(postUrl: String) : String {
        return try {
            val splittedUrl = postUrl.split((NetworkManager.BASE_URL + "r/").toRegex())
            splittedUrl[1]
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: ${e.message}")
            ""
        }
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

    fun makePostCommentRequest(comment: String, username: String, modhash: String, cookie: String, postId: String) {
        val headerMap = HashMap<String, String>()
        headerMap["User-Agent"] = username
        headerMap["X-Modhash"] = modhash
        headerMap["cookie"] = "reddit_session=$cookie"

        launch(UI) {
            try {
                val isResponseSuccessful = initRetrofit(GsonConverterFactory.create())
                        .submitComment(headerMap, "comment", postId, comment).await().success.toBoolean()
                return@launch if (isResponseSuccessful) view.handleSuccessPostComment() else view.handleUnsuccessPostComment()
            } catch (e: Exception) {
                Log.e(TAG, "onFailure: Unable to post comment: ${e.message}")
                view.showPostCommentError()
            }
        }
    }

    interface View {

        fun showLoadingComments()

        fun hideLoadingComments()

        fun showComments(comments: ArrayList<Comment>)

        fun showCommentsError()

        fun handleSuccessPostComment()

        fun handleUnsuccessPostComment()

        fun showPostCommentError()
    }
}