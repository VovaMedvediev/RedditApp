package com.example.vmedvediev.redditapp.presenter

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.vmedvediev.redditapp.Account.LoginActivity
import com.example.vmedvediev.redditapp.model.NetworkManager
import com.example.vmedvediev.redditapp.model.Post
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.converter.gson.GsonConverterFactory

class LoginPresenter(private val view: LoginPresenter.LoginView) {

    companion object {
        private const val TAG = "PostsPresenter"
    }

    //This function should be named "login" because its name used in the request.
    fun login(username: String, password: String) = launch(UI) {
        if (validateCredentials(username, password)) {
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
        } else {
            view.shoeEmptyFieldError()
        }
    }

    private fun validateCredentials(username: String, password: String) : Boolean {
        return (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password))
    }

    interface LoginView {

        fun showLoading()

        fun hideLoading()

        fun handleSuccessfulLogin(modhash: String?, username: String, cookie: String?)

        fun showLoginError()

        fun shoeEmptyFieldError()
    }
}