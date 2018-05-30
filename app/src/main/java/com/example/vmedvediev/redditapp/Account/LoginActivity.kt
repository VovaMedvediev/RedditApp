package com.example.vmedvediev.redditapp.Account

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.example.vmedvediev.redditapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginRequestLoadingProgressBar?.visibility = View.GONE

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                loginRequestLoadingProgressBar?.visibility = View.GONE
            }
        }
    }
}