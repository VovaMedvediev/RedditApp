package com.example.vmedvediev.redditapp

import android.util.Log

class ExtractXML constructor(val xml: String?, val tag: String) {

    constructor(tag: String, xml: String?, endTag: String?) : this(xml, tag) {
        this.endTag = endTag
    }

    companion object {
        private const val TAG = "ExtractXML"
    }

    var endTag: String? = "NONE"

    fun parseHtml() : MutableList<String> {
        val result = ArrayList<String>()

        var splitXml: List<String>? = null
        var marker: String? = null

        if (endTag == "NONE") {
            marker = "\""
            //Log.d(TAG, "XML BEFORE SPLIT: $xml")
            splitXml = xml?.split(tag + marker)
        } else {
            marker = endTag
            //Log.d(TAG, "XML BEFORE SPLIT: $xml")
            splitXml = xml?.split(tag)
        }

        Log.e(TAG, "XML AFTER SPLIT: ${splitXml.toString()}")
        val index = splitXml!![1].indexOf(marker!!)
        val test = splitXml!![1].substring(0, index)
        Log.d(TAG, "A:SFNAS:GJNS:ALNG:ASNF:SNG:LSAJG:LASG:LASNG:LSA:LGNA:SNG:ASGASG: $test")
        result.add(test)

//        for (item in splitXml!!) {
//            Log.e(TAG, "---------------------------------------------")
//            Log.e(TAG, "XML AFTER SPLIT: $item")
//            var temp = item
//            val index = temp.indexOf(marker!!)
//            //temp = temp.substring(0, index)
//            result.add(temp)
//        }

            return result
    }

//    fun start(): MutableList<String> {
//        val result = java.util.ArrayList<String>()
//        var splitXML: Array<String>? = null
//        var marker: String? = null
//
//        Log.e(TAG, "XML: $xml")
//
//        if (endTag == "NONE") {
//            marker = "\""
//            splitXML = xml?.split((tag + marker).toRegex())?.toTypedArray()
//            Log.e(TAG, "SPLITED: ${splitXML?.joinToString("**********")}")
//            //)?.dropLastWhile { it.isEmpty() }?.toTypedArray()
//        } else {
//            marker = endTag
//            splitXML = xml?.split(tag.toRegex())?.toTypedArray()
//            Log.e(TAG, "SPLITTED 2: ${splitXML?.joinToString("**********")}")
//            //dropLastWhile { it.isEmpty() }?.toTypedArray()
//        }
//        val count = splitXML?.size
//
//        for (i in 1 until count!!) {
//            var temp = splitXML?.get(i)
//            val index = temp?.indexOf(marker!!)
//            temp = temp?.substring(0, index!!)
//            result.add(temp!!)
//        }
//        result.forEach {
//            Log.e(TAG, "item = $it")
//        }
//        return result
//    }
}