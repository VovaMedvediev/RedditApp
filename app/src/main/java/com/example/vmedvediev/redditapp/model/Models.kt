package com.example.vmedvediev.redditapp.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "feed", strict = false)
data class Feed(@field:Element(name = "icon") var icon: String = "",
                @field:Element(name = "id") var id: String = "",
                @field:Element(name = "logo") var logo: String = "",
                @field:Element(name = "title") var title: String = "",
                @field:Element(name = "updated") var updated: String = "",
                @field:Element(name = "subtitle") var subtitle: String = "",
                @field:ElementList(name = "entry", inline = true) var entrys: List<Entry>? = null) : Serializable {

    override fun toString(): String {
        return "Feed: \n [Entrys: \n" + entrys +"]"
    }
}

@Root(name = "entry", strict = false)
data class Entry(@field:Element(name = "content") var content: String = "",
                 @field:Element(name = "author", required = false) var author: Author? = null,
                 @field:Element(name = "title") var title: String = "",
                 @field:Element(name = "updated") var updated: String = "",
                 @field:Element(name = "id") var id: String = "") : Serializable {

    override fun toString(): String {
        return "\n\nEntry{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", id='" + id + '\'' +
                ", title=" + title + '\'' +
                ", updated='" + updated + '\'' +
                '}' + "\n" +
                "------------------------------------------------------------------------------------------------- \n"
    }
}

@Root(name = "author", strict = false)
data class Author(@field:Element(name = "name") var name: String = "",
                  @field:Element(name = "uri") var uri: String = "") : Serializable {

    override fun toString(): String {
        return "Author{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                '}'
    }
}

data class Post(val title: String, val author: String?, val dateUpdated: String, val postUrl: String, val thumnailUrl:String)

data class Comment(val comment: String, val author: String?, val updated: String, val id: String)