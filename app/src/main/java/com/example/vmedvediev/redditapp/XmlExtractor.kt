package com.example.vmedvediev.redditapp


class XmlExtractor constructor(val xml: String?, val tag: String) {

    constructor(tag: String, xml: String?, endTag: String?) : this(xml, tag) {
        this.endTag = endTag
    }

    var endTag: String? = "NONE"

    fun parseHtml() : MutableList<String> {
        val result = ArrayList<String>()

        var splitXml: List<String> = ArrayList()
        var marker: String? = null

        xml?.let {
            if (endTag == "NONE") {
                marker = "\""
                splitXml = xml.split(tag + marker)
            } else {
                marker = endTag
                splitXml = xml.split(tag)
            }
        }

        val index = splitXml[1].indexOf(marker!!)
        val test = splitXml[1].substring(0, index)
        result.add(test)

        return result
    }
}