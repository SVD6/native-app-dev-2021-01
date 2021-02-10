package com.silverorange.videoplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.GsonBuilder
import com.silverorange.videoplayer.databinding.ActivityMainBinding
import com.silverorange.videoplayer.util.objects.Author
import com.silverorange.videoplayer.util.objects.VideoObject
import io.noties.markwon.Markwon
import org.json.JSONArray
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    private lateinit var player: SimpleExoPlayer
    private lateinit var markwon: Markwon

    private lateinit var videos: MutableList<VideoObject>

    private var showControls = false
    private var currentVideo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind layout
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Initialize Player
        player = SimpleExoPlayer.Builder(this).build()
        bind.player.player = player

        // Initialize Markdown
        markwon = Markwon.create(this)

        // Load Videos
        videos = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            loadData(intent.getStringExtra("jsonData")!!)
        }

        bind.playPause.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                bind.playPause.setImageResource(R.drawable.ic_play)
            } else {
                player.play()
                bind.playPause.setImageResource(R.drawable.ic_pause)
            }
        }

        bind.playerLayout.setOnClickListener {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
            if (showControls) {
                bind.playPause.visibility = View.GONE
                bind.next.visibility = View.GONE
                bind.previous.visibility = View.GONE
                showControls = false
            } else {
                bind.playPause.visibility = View.VISIBLE
                bind.next.visibility = View.VISIBLE
                bind.previous.visibility = View.VISIBLE
                showControls = true
            }
        }

        bind.next.setOnClickListener {
            nextVideo()
        }

        bind.previous.setOnClickListener {
            previousVideo()
        }
    }

    private fun nextVideo() {
        Toast.makeText(this, "Next Video", Toast.LENGTH_SHORT).show()
        if ((currentVideo + 1) <= videos.size) {
            player.stop()
            loadVideo(videos[currentVideo + 1], currentVideo + 1)
            currentVideo++
        } else {
            Toast.makeText(this, "No more videos!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun previousVideo() {
        if ((currentVideo - 1) >= 0) {
            player.stop()
            loadVideo(videos[currentVideo - 1], currentVideo - 1)
            currentVideo--
        } else {
            Toast.makeText(this, "This is the first video!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData(jsonData: String) {
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
        loadVideo(videos[0], 0)

        // Attempt Margin fix
        val params = bind.playPause.layoutParams as ViewGroup.MarginLayoutParams
        val playerHeight = bind.player.height
        params.setMargins(0, playerHeight / 2, 0, 0)
        bind.playPause.layoutParams = params
    }

    private fun loadVideo(video: VideoObject, position: Int) {
        // Load video to player
        player.setMediaItem(MediaItem.fromUri(video.hlsURL!!))
        player.prepare()

        // Set the video details
        bind.videoTitle.text = video.title
        bind.videoAuthor.text = video.author!!.name
        markwon.setMarkdown(bind.videoDetails, video.description!!)

        // Next/Prev button logic
//        if (position == 0) {
//            bind.previous.isEnabled = false
//        }
//        if (position == videos.size - 1) {
//            bind.next.isEnabled = false
//        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}