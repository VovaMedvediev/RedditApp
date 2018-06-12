package com.example.vmedvediev.redditapp.Account

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.vmedvediev.redditapp.model.NetworkManager.API_TYPE
import com.example.vmedvediev.redditapp.model.NetworkManager.initRetrofit
import com.example.vmedvediev.redditapp.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CommentsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginRequestLoadingProgressBar?.visibility = View.GONE

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                loginRequestLoadingProgressBar?.visibility = View.VISIBLE

                login(username, password)
            }
        }
    }

    //This function should be named "login" because its name used in the request.
    private fun login(username: String, password: String) = launch(UI) {
            try {
                initRetrofit(GsonConverterFactory.create())
                        .signIn(username, username, password, API_TYPE).await().json.data.let {
                    val (modhash, cookie) = it
                    handleSuccessfullLogin(modhash, username, cookie)
                }

            } catch (e: Exception) {
                Log.e(LoginActivity.TAG, "onFailure: Unable to login: ${e.message}")
                Toast.makeText(this@LoginActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
            }
        }


    private fun handleSuccessfullLogin(modhash: String?, username: String, cookie: String?) {
        if (!TextUtils.isEmpty(modhash)) {
            setSessionParams(username, modhash, cookie)
            loginRequestLoadingProgressBar?.visibility = View.GONE
            Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

            // Navigate back to previous activity
            finish()
        }
    }

    private fun setSessionParams(username: String, modhash: String?, cookie: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.apply {
            putString(getString(R.string.SessionUsername), username )
            putString(getString(R.string.SessionModhash), modhash)
            putString(getString(R.string.SessionCookie), cookie)
            apply()
        }
    }
}