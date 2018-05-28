package com.example.vmedvediev.redditapp.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "feed", strict = false)
data class Feed @JvmOverloads constructor(@field:Element(name = "icon") var icon: String = "",
                @field:Element(name = "id") var id: String = "",
                @field:Element(name = "logo") var logo: String = "",
                @field:Element(name = "title") var title: String = "",
                @field:Element(name = "updated") var updated: String = "",
                @field:Element(name = "subtitle") var subtitle: String = "",
                @field:ElementList(name = "icon", inline = true) var entrys: List<Entry>? = null) : Serializable

@Root(name = "entry", strict = false)
data class Entry @JvmOverloads constructor(@field:Element(name = "content") var content: String = "",
                 @field:Element(name = "author", required = false) var author: Author? = null,
                 @field:Element(name = "title") var title: String = "",
                 @field:Element(name = "updated") var updated: String = "",
                 @field:Element(name = "id") var id: String = "") : Serializable

@Root(name = "author", strict = false)
data class Author(@field:Element(name = "name") var name: String = "",
                  @field:Element(name = "uri") var uri: String = "") : Serializable