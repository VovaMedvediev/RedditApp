package com.example.vmedvediev.redditapp.model

import android.util.Log


class XmlExtractor constructor(val xml: String?, val tag: String) {

    companion object {
        private const val TAG = "XmlExtractor"
    }

    constructor(xml: String?, tag: String, endTag: String) : this(xml, tag) {
        this.endTag = endTag
    }

    var endTag: String = "NONE"

    fun parseHtml() : MutableList<String> {
        val result = ArrayList<String>()

        var splitXml: List<String> = ArrayList()
        lateinit var marker: String

        xml?.let {
            if (endTag == "NONE") {
                marker = "\""
                splitXml = it.split(tag + marker)
                val index = splitXml[1].indexOf(marker)
                val test = splitXml[1].substring(0, index)
                result.add(test)
            } else {
                marker = endTag
                splitXml = it.split(tag)

                splitXml.forEach {
                    try {
                        var temp = it
                        val index = temp.indexOf(marker)
                        temp = temp.substring(0, index)
                        result.add(temp)
                    } catch (e: StringIndexOutOfBoundsException) {
                     Log.e(TAG, "parseHtml: STRING INDEX OUT OF BOUNDS EXCEPTION")
                    }
                }
            }
        }
        return result
    }
}