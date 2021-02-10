package com.silverorange.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.silverorange.videoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
    }
}