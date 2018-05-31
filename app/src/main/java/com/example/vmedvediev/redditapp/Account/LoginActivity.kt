package com.example.vmedvediev.redditapp.Account

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.vmedvediev.redditapp.NetworkManager.API_TYPE
import com.example.vmedvediev.redditapp.NetworkManager.initGsonRetrofit
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.model.LoginChecker
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private fun login(username: String, password: String) {
        val call = initGsonRetrofit().signIn(username, username, password, API_TYPE)
        call.enqueue(object : Callback<LoginChecker> {
            override fun onResponse(call: Call<LoginChecker>?, response: Response<LoginChecker>?) {
                Log.d(TAG, "onResponse: Server Response: ${response.toString()}")

                val data = response?.body()?.json?.data
                val modhash = data?.modhash
                val cookie = data?.cookie

                Log.d(TAG, "$modhash $cookie")

                handleSuccessfullLogin(modhash, username, cookie)
            }

            override fun onFailure(call: Call<LoginChecker>?, t: Throwable?) {
                loginRequestLoadingProgressBar?.visibility = View.GONE
                Log.e(LoginActivity.TAG, "onFailure: Unable to login: ${t?.message}")
                Toast.makeText(this@LoginActivity, "An Error Occured!", Toast.LENGTH_SHORT).show()
            }
        })
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