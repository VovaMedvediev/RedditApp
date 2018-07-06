package com.example.vmedvediev.redditapp.presenter

import android.util.Log
import com.example.vmedvediev.redditapp.CoroutineContextProvider
import com.example.vmedvediev.redditapp.model.NetworkManager
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.converter.gson.GsonConverterFactory

class LoginPresenter(private val view: LoginPresenter.View, private val contextPool: CoroutineContextProvider = CoroutineContextProvider()) {

    //This function should be named "login" because its name used in the request.
    fun login(username: String, password: String) {
        if (validateCredentials(username, password)) {
            startLogin(username, password)
        } else {
            view.shoeEmptyFieldError()
        }
    }

    private fun validateCredentials(username: String, password: String) : Boolean {
        return (username.isNotEmpty() && password.isNotEmpty())
    }

    fun startLogin(username: String, password: String) : Pair<String?, String?> {
        var modhash: String? = ""
        var cookie: String? = ""
        launch(contextPool.Main) {
            try {
                view.showLoading()
                val data = withContext(contextPool.IO) {
                    return@withContext NetworkManager.initRetrofit(GsonConverterFactory.create())
                            .signIn(username, username, password, NetworkManager.API_TYPE).execute().body()?.json?.data
                }
                modhash = data?.modhash
                cookie = data?.cookie
                if (modhash.isNullOrEmpty()) throw Exception()
                view.handleSuccessfulLogin(modhash, username, cookie)
                view.hideLoading()
            } catch (e: Exception) {
                view.showLoginError()
                view.hideLoading()
            }
        }
        return Pair(modhash, cookie)
    }

    interface View {

        fun showLoading()

        fun hideLoading()

        fun handleSuccessfulLogin(modhash: String?, username: String, cookie: String?)

        fun showLoginError()

        fun shoeEmptyFieldError()
    }
}