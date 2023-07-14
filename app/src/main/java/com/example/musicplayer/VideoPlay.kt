package com.example.musicplayer

import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.musicplayer.databinding.ActivityVideoPlayBinding
import com.example.musicplayer.model.VideoDataClass
import com.example.musicplayer.viewmodel.VideoViewModel

@UnstableApi
class VideoPlay : AppCompatActivity() {
    lateinit var binding: ActivityVideoPlayBinding
    private lateinit var exoPlayer: ExoPlayer
    lateinit var viewModel: VideoViewModel
    private var position = 0
    private lateinit var title: String
    lateinit var bundle: Bundle
    var videoPlayList = arrayListOf<VideoDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[VideoViewModel::class.java]
        viewModel.setFullScreen(this)

        position = intent.getIntExtra("position", 1)
        title = intent.getStringExtra("displayName").toString()
        findViewById<TextView>(R.id.txt_videoTitle).text = title

        bundle = intent.getBundleExtra("bundle")!!
        videoPlayList = bundle.getParcelableArrayList("data")!!

        initializePlayer()
    }

    fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.videoPlayerView.apply {
            player = exoPlayer
            keepScreenOn = true
        }
        viewModel.playVideo(this, exoPlayer, videoPlayList, position)
        videoController()
    }

    private fun videoController() {
        findViewById<ImageView>(R.id.imgV_back).setOnClickListener {
            finish()
        }
        findViewById<ImageView>(R.id.imgV_forward).setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition + 5000)
        }

        findViewById<ImageView>(R.id.imgV_replay).setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition - 5000)
        }

        findViewById<ImageView>(R.id.imgV_next).setOnClickListener {
            try {
                exoPlayer.stop()
                position++
                findViewById<TextView>(R.id.txt_videoTitle).text =
                    videoPlayList[position].displayName
                initializePlayer()
            } catch (e: Exception) {
                finish()
            }
        }

        findViewById<ImageView>(R.id.imgV_previous).setOnClickListener {
            try {
                exoPlayer.stop()
                position--
                findViewById<TextView>(R.id.txt_videoTitle).text =
                    videoPlayList[position].displayName
                initializePlayer()
            } catch (e: Exception) {
                finish()
            }
        }

        findViewById<ImageView>(R.id.imgV_playPause).setOnClickListener {
            if (exoPlayer.playWhenReady) {
                exoPlayer.playWhenReady = false
                exoPlayer.pause()
                Glide.with(this).load(R.drawable.ic_play_button)
                    .into(findViewById(R.id.imgV_playPause))
            } else {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
                Glide.with(this).load(R.drawable.ic_pause_button)
                    .into(findViewById(R.id.imgV_playPause))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.apply {
            playWhenReady = false
            pause()
            playbackState
        }
        Glide.with(this).load(R.drawable.ic_play_button).into(findViewById(R.id.imgV_playPause))
    }

    override fun onRestart() {
        super.onRestart()
        exoPlayer.apply {
            playWhenReady = true
            playbackState
        }
    }
}