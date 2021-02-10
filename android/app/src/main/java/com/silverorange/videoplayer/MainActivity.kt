package com.silverorange.videoplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.GsonBuilder
import com.silverorange.videoplayer.databinding.ActivityMainBinding
import com.silverorange.videoplayer.util.objects.Author
import com.silverorange.videoplayer.util.objects.VideoObject
import org.json.JSONArray
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    private lateinit var player: SimpleExoPlayer

    private lateinit var videos: MutableList<VideoObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind layout
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Initialize Player
        player = SimpleExoPlayer.Builder(this).build()
        bind.player.player = player

        // Load Videos
        videos = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            loadVideos(intent.getStringExtra("jsonData")!!)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadVideos(jsonData: String) {
        val jsonArray = JSONArray(jsonData)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val video = VideoObject()
            video.id = jsonObject.optString("id")
            video.title = jsonObject.optString("title")
            video.hlsURL = jsonObject.optString("hlsURL")
            video.fullURL = jsonObject.optString("fullURL")
            video.description = jsonObject.optString("description")
            val dateParser = DateTimeFormatter.ISO_DATE_TIME
            video.publishedAt = dateParser.parse(jsonObject.optString("publishedAt")).toString()
            video.author = GsonBuilder().create()
                .fromJson(jsonObject.optString("author"), Author::class.java)
            videos.add(i, video)
        }

        // Sort Videos
        videos.sortBy { it.publishedAt }

        // Load First Video

    }
}