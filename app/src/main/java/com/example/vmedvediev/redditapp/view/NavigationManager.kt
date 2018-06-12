package com.example.vmedvediev.redditapp.view

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.example.vmedvediev.redditapp.Account.LoginActivity
import com.example.vmedvediev.redditapp.R

object NavigationManager {

    fun changeScreen(context: Context, itemId: Int): Intent? {
        return when(itemId) {
            R.id.navigationLogin -> Intent(context, LoginActivity::class.java)
            else -> null
        }
    }
}