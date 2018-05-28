package com.example.vmedvediev.redditapp

import android.util.Log

class ExtractXML(val tag: String, val xml: String?) {

    companion object {
        private const val TAG = "ExtractXML"
    }

    fun start() : MutableList<String> {
        val result = ArrayList<String>()

        val splitXml = xml?.split(tag + "\"")
        val count = splitXml?.size

        for (item in splitXml!!) {
            val index = item.indexOf("\"")

            Log.d(TAG, "start: index: $index")
            Log.d(TAG, "start: extracted: $item")

            val snippedItem: String = item.substring(0, index)
            Log.d(TAG, "start: snipped: $snippedItem")
            result.add(snippedItem)
        }
        return result
    }
}