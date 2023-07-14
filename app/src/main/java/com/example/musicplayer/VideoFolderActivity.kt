package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.databinding.ActivityVideoFolderBinding
import com.example.musicplayer.viewmodel.VideoViewModel

class VideoFolderActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoFolderBinding
    lateinit var viewModel: VideoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[VideoViewModel::class.java]
        viewModel.permission(this,binding, contentResolver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.permission(this, binding, contentResolver)
    }
}