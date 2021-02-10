package com.silverorange.videoplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.silverorange.videoplayer.databinding.ActivitySplashBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    private lateinit var bind: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        val client = OkHttpClient()

        thread {
            val request = Request.Builder()
                .url(API_URL)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonData = response.body!!.string()
                    Handler(Looper.getMainLooper()).postDelayed({
                        launchMainActivity(jsonData)
                    }, 1000)
                } else {
                    throw IOException("Unexpected code $response")
                }
            }
        }
    }

    private fun launchMainActivity(jsonData: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("jsonData", jsonData)
        startActivity(intent)
    }

    companion object {
        private const val API_URL = "http://192.168.2.73:8000/videos"
    }
}