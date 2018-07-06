package com.example.vmedvediev.redditapp

import com.example.vmedvediev.redditapp.presenter.LoginPresenter
import org.junit.Test
import org.mockito.Mockito

class LoginPresenterTest {

    @Test
    fun testLoginError() {
        val view = Mockito.mock(LoginPresenter.View::class.java)
        val loginPresenter = LoginPresenter(view, TestContextProvider())
        loginPresenter.startLogin("test", "test")
        Mockito.verify(view).showLoginError()
    }

    @Test
    fun testLogin() {
        val view = Mockito.mock(LoginPresenter.View::class.java)
        val loginPresenter = LoginPresenter(view, TestContextProvider())
        val expectedResult = loginPresenter.startLogin("skyvovker", "vovchik456")
        Mockito.verify(view).handleSuccessfulLogin(expectedResult.first, "skyvovker", expectedResult.second)
    }
}

