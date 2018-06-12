package com.example.vmedvediev.redditapp

import android.content.Context
import android.content.Intent
import com.example.vmedvediev.redditapp.view.LoginViewActivity
import com.example.vmedvediev.redditapp.view.PostsViewActivity

object NavigationManager {

    fun changeScreen(context: Context, itemId: Int): Intent? {
        when(itemId) {
            R.id.navigationLogin -> { return Intent(context, LoginViewActivity::class.java) }
        }
        return null
    }
}