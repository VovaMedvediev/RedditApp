package com.example.vmedvediev.redditapp

import kotlinx.coroutines.experimental.Unconfined
import kotlin.coroutines.experimental.CoroutineContext

class TestContextProvider : CoroutineContextProvider() {
    override val Main: CoroutineContext = Unconfined
    override val IO: CoroutineContext = Unconfined
}