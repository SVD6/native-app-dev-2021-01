package com.silverorange.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.SimpleExoPlayer
import com.silverorange.videoplayer.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.concurrent.thread

private const val API_URL = "http://192.168.2.73:8000/videos"

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    private lateinit var player: SimpleExoPlayer
    private lateinit var client: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind layout
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Initialize Player
        player = SimpleExoPlayer.Builder(this).build()
        bind.player.player = player

        // Initialize OkHttp
        client = OkHttpClient()
        thread {
            run()
        }
    }

    private fun run() {
        val request = Request.Builder()
            .url(API_URL)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                Log.i("response", response.body!!.string())
            } else {
                throw IOException("Unexpected code $response")
            }
        }
    }
}