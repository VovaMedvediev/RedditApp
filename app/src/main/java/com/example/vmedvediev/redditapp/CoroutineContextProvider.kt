package com.example.vmedvediev.redditapp

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

open class CoroutineContextProvider() {
    open val Main: CoroutineContext by lazy { UI }
    open val IO: CoroutineContext by lazy { CommonPool }
}