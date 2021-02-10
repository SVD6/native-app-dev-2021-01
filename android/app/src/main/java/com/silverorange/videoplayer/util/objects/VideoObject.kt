package com.silverorange.videoplayer.util.objects
import java.util.*

class VideoObject {
    var id: String? = null
    var title: String? = null
    var hlsURL: String? = null
    var fullURL: String? = null
    var description: String? = null
    var publishedAt: Any? = null
    var author: Author? = null
}

class Author {
    var id: String? = null
    var name: String? = null
}