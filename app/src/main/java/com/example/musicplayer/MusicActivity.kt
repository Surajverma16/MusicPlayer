package com.example.musicplayer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.adapter.AudioAdapter
import com.example.musicplayer.databinding.ActivityAudioBinding
import com.example.musicplayer.viewmodel.MusicViewModel

class MusicActivity : AppCompatActivity() {
    lateinit var binding: ActivityAudioBinding
    lateinit var viewModel: MusicViewModel
    lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]
        player = ExoPlayer.Builder(this@MusicActivity).build()
        viewModel.permission(player, this, binding, contentResolver)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.displayAudioFiles(player,this,binding, contentResolver)
    }

    override fun onBackPressed() {
        if(binding.included.customLayout.visibility == View.VISIBLE){
            binding.included.customLayout.visibility = View.GONE
            binding.musicListLayout.visibility = View.VISIBLE
            viewModel.bottomMusicController(this,binding, player, true)
        }else{
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }
}











