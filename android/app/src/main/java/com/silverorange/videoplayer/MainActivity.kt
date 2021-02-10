package com.silverorange.videoplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

    private var prevButtonAlpha: Float = 1f
    private var nextButtonAlpha: Float = 1f

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

        // Button Logic
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
            showControls = if (showControls) {
                hideControls()
                false
            } else {
                showControls()
                true
            }
        }

        bind.next.setOnClickListener {
            nextVideo()
        }

        bind.previous.setOnClickListener {
            previousVideo()
        }
    }

    // Load the data from the API request into an list of VideoObjects
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData(jsonData: String) {
        val jsonArray = JSONArray(jsonData)
        // Create new VideoObject for each jsonObject in the jsonArray
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
    }

    // Function for loading a new video into the player view + filling in details
    private fun loadVideo(video: VideoObject, position: Int) {
        // Load video to player
        player.setMediaItem(MediaItem.fromUri(video.hlsURL!!))
        player.prepare()

        // Set the video details
        bind.videoTitle.text = video.title
        bind.videoAuthor.text = video.author!!.name
        markwon.setMarkdown(bind.videoDescription, video.description!!)

        showControls = true

        // Next/Previous enable or disable based on position
        when (position) {
            0 -> {
                bind.previous.alpha = 0.6f
                bind.next.alpha = 1f
            }
            (videos.size - 1) -> {
                bind.next.alpha = 0.6F
                bind.previous.alpha = 1f
            }
            else -> {
                bind.next.alpha = 1f
                bind.previous.alpha = 1f
            }
        }
    }

    // When player is clicked and controls are hidden, show them
    private fun showControls() {
        bind.next.alpha = nextButtonAlpha
        bind.previous.alpha = prevButtonAlpha

        bind.playPause.visibility = View.VISIBLE
        bind.next.visibility = View.VISIBLE
        bind.previous.visibility = View.VISIBLE
    }

    // When player is clicked and controls are visible, hide them
    private fun hideControls() {
        prevButtonAlpha = bind.previous.alpha
        nextButtonAlpha = bind.next.alpha

        bind.playPause.visibility = View.GONE
        bind.next.visibility = View.GONE
        bind.previous.visibility = View.GONE
    }

    // Next button must load the next video
    private fun nextVideo() {
        if ((currentVideo + 1) < videos.size) {
            player.stop()
            loadVideo(videos[currentVideo + 1], currentVideo + 1)
            currentVideo++
        } else {
            Toast.makeText(this, "This is the last video!", Toast.LENGTH_SHORT).show()
            bind.next.alpha = 0.6f
        }
    }

    // Previous button must load the previous video
    private fun previousVideo() {
        if ((currentVideo - 1) >= 0) {
            player.stop()
            loadVideo(videos[currentVideo - 1], currentVideo - 1)
            currentVideo--
        } else {
            Toast.makeText(this, "This is the first video!", Toast.LENGTH_SHORT).show()
            bind.previous.alpha = 0.6f
        }
    }
}