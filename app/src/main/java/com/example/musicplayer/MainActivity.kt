package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAudioFolder.setOnClickListener {
            startActivity(Intent(this, MusicActivity::class.java))
        }

        binding.imgVideoFolder.setOnClickListener {
            startActivity(Intent(this, VideoFolderActivity::class.java))
        }
    }

}