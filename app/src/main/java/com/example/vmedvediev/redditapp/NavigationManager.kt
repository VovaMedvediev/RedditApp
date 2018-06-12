package com.example.vmedvediev.redditapp

import android.content.Context
import android.content.Intent
import com.example.vmedvediev.redditapp.view.LoginViewActivity

object NavigationManager {

    fun changeScreen(context: Context, itemId: Int): Intent? {
        return when(itemId) {
            R.id.navigationLogin -> Intent(context, LoginViewActivity::class.java)
            else -> null
        }
    }
}