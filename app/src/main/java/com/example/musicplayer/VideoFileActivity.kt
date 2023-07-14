package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.databinding.ActivityVideoFileBinding
import com.example.musicplayer.viewmodel.VideoViewModel

class VideoFileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoFileBinding
    private lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val folderName = intent.getStringExtra("folderName").toString()
        supportActionBar!!.title = folderName

        viewModel = ViewModelProvider(this)[VideoViewModel::class.java]
        viewModel.showVideoFiles(folderName,this,binding,contentResolver)
    }
}