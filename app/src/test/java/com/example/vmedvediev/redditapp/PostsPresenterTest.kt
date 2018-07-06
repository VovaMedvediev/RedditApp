package com.example.vmedvediev.redditapp

import com.example.vmedvediev.redditapp.model.Post
import com.example.vmedvediev.redditapp.presenter.PostsPresenter
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class PostsPresenterTest {

    @Test
    fun testGetPosts() {
        val view = Mockito.mock(PostsPresenter.View::class.java)
        val presenter = PostsPresenter(view, TestContextProvider())
        val expectedResult = presenter.getPosts("funny")
        Mockito.verify(view).showPosts(expectedResult)
    }

    @Test
    fun testGetPostsError() {
        val view = Mockito.mock(PostsPresenter.View::class.java)
        val presenter = PostsPresenter(view, TestContextProvider())
        presenter.getPosts("test")
        Mockito.verify(view).showError()
    }
}