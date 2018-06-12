package com.example.vmedvediev.redditapp.view

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View.*
import android.widget.Toast
import com.example.vmedvediev.redditapp.R
import com.example.vmedvediev.redditapp.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

class LoginViewActivity : AppCompatActivity(), LoginPresenter.View {

    private val presenter: LoginPresenter by lazy {
        LoginPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            presenter.login(username, password)
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

    override fun showLoading() {
        loginRequestLoadingProgressBar?.visibility = VISIBLE
    }

    override fun hideLoading() {
        loginRequestLoadingProgressBar?.visibility = GONE
    }

    override fun handleSuccessfulLogin(modhash: String?, username: String, cookie: String?) {
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
        setSessionParams(username, modhash, cookie)

        // Navigate back to previous activity
        finish()
    }

    override fun showLoginError() {
        Toast.makeText(this, "An Error Occurred!", Toast.LENGTH_SHORT).show()
    }

    override fun shoeEmptyFieldError() {
        Toast.makeText(this, "Email or password shouldn't be empty!", Toast.LENGTH_SHORT).show()
    }
}