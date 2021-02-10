package com.silverorange.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.GsonBuilder
import com.silverorange.videoplayer.databinding.ActivityMainBinding
import com.silverorange.videoplayer.util.objects.Author
import com.silverorange.videoplayer.util.objects.VideoObject
import org.json.JSONArray

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
        loadVideos(intent.getStringExtra("jsonData")!!)
    }

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
            video.publishedAt = jsonObject.optString("publishedAt")
            video.author = GsonBuilder().create()
                .fromJson(jsonObject.optString("author"), Author::class.java)
            videos.add(i, video)
        }
    }
}