package com.example.vmedvediev.redditapp.presenter

import android.text.TextUtils
import android.util.Log
import com.example.vmedvediev.redditapp.model.NetworkManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.converter.gson.GsonConverterFactory

class LoginPresenter(private val view: LoginPresenter.View) {

    companion object {
        private const val TAG = "PostsPresenter"
    }

    //This function should be named "login" because its name used in the request.
    fun login(username: String, password: String) {
        if (validateCredentials(username, password)) {
            startLogin(username, password)
        } else {
            view.shoeEmptyFieldError()
        }
    }

    private fun validateCredentials(username: String, password: String) : Boolean {
        return (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password))
    }

    private fun startLogin(username: String, password: String) = launch(UI) {
        view.showLoading()
        try {
            NetworkManager.initRetrofit(GsonConverterFactory.create())
                    .signIn(username, username, password, NetworkManager.API_TYPE).await().json.data.let {
                val (modhash, cookie) = it
                view.handleSuccessfulLogin(modhash, username, cookie)
                view.hideLoading()
            }
        } catch (e: Exception) {
            Log.e(TAG, "onFailure: Unable to login: ${e.message}")
            view.showLoginError()
            view.hideLoading()
        }
    }

    interface View {

        fun showLoading()

        fun hideLoading()

        fun handleSuccessfulLogin(modhash: String?, username: String, cookie: String?)

        fun showLoginError()

        fun shoeEmptyFieldError()
    }
}