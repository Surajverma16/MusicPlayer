package com.example.musicplayer.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.adapter.AudioAdapter
import com.example.musicplayer.databinding.ActivityAudioBinding
import com.example.musicplayer.model.MusicDataClass
import java.util.concurrent.TimeUnit

class MusicViewModel : ViewModel() {

    private lateinit var audioAdapter: AudioAdapter
    private var isMusicPlaying = false
    private var isPlaying = true
    var musicPosition = 0
    private var musicList = arrayListOf<MusicDataClass>()
    val PERMISSION_REQUEST_CODE = 123

    fun permission(
        player: ExoPlayer,
        activity: Activity,
        binding: ActivityAudioBinding,
        contentResolver: ContentResolver,
    ) {
        if (checkPermission(activity)) {
            displayAudioFiles(player, activity, binding, contentResolver)
        } else {
            requestPermission(activity)
        }
    }

    private fun checkPermission(activity: Activity): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    fun displayAudioFiles(
        player: ExoPlayer,
        activity: Activity,
        binding: ActivityAudioBinding,
        contentResolver: ContentResolver,
    ) {
        val audioFiles = getAudioFilesFromDevice(contentResolver)
        audioAdapter = AudioAdapter(
            audioFiles,
            player,
            activity,
            object : AudioAdapter.clickedMusic {
                override fun playMusic(audioFiles: ArrayList<MusicDataClass>, position: Int) {
                    musicPosition = position
                    musicList = audioFiles
                    isPlaying = true
                    customMusicLayout(activity, binding, player)
                }
            })
        binding.audioRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.audioRecyclerView.adapter = audioAdapter
    }

    @SuppressLint("Range")
    private fun getAudioFilesFromDevice(contentResolver: ContentResolver): ArrayList<MusicDataClass> {
        val audioFiles = arrayListOf<MusicDataClass>()
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        /* val selection = "${ Media.DATA } LIKE ?"
         val projection = arrayOf(MediaStore.Audio.Media.DISPLAY_NAME)*/

        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val fileName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val albumId =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val duration =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val data = MusicDataClass(fileName, albumId, path, artist, duration)
                audioFiles.add(data)
            }
            cursor.close()
        }
        return audioFiles
    }

    fun displayTime(millisecond: Double): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisecond.toLong())
        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(millisecond.toLong()) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millisecond.toLong())
            )
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(millisecond.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millisecond.toLong())
            )

        Log.i("hours", hours.toString())
        Log.i("minutes", minutes.toString())
        Log.i("seconds", seconds.toString())

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun customMusicLayout(activity: Activity, binding: ActivityAudioBinding, player: ExoPlayer) {
        binding.included.customLayout.visibility = View.VISIBLE
        binding.musicListLayout.visibility = View.GONE

        binding.included.imgBackButton.setOnClickListener {
            binding.included.customLayout.visibility = View.GONE
            binding.musicListLayout.visibility = View.VISIBLE
            isMusicPlaying = true
            bottomMusicController(activity, binding, player, isMusicPlaying)
        }

        binding.included.exoPlayPause.setOnClickListener {
            if (isPlaying) {
                binding.included.exoPlayPause.setImageResource(R.drawable.ic_play_button)
                player.pause()
                isPlaying = !isPlaying
            } else {
                binding.included.exoPlayPause.setImageResource(R.drawable.ic_pause_button)
                player.play()
                isPlaying = !isPlaying
            }
        }

        binding.included.exoNext.setOnClickListener {
            binding.included.exoPlayPause.setImageResource(R.drawable.ic_pause_button)
            player.play()
            isPlaying = !isPlaying

            if (player.isPlaying) {
                player.pause()
            }
            binding.included.exoProgress.progress = 0
            musicPosition++
            if (musicPosition != musicList.size) {
                musicPosition
            } else {
                musicPosition = 0
            }
            playMusic(activity, binding, player)
        }

        binding.included.exoPrev.setOnClickListener {
            binding.included.exoPlayPause.setImageResource(R.drawable.ic_pause_button)
            player.play()
            isPlaying = !isPlaying

            if (player.isPlaying) {
                player.pause()
            }
            binding.included.exoProgress.progress = 0
            musicPosition--
            if (musicPosition < 0) {
                musicPosition = musicList.size - 1
            } else {
                musicPosition
            }

            playMusic(activity, binding, player)
        }
        playMusic(activity, binding, player)
    }

    fun bottomMusicController(
        activity: Activity,
        binding: ActivityAudioBinding,
        player: ExoPlayer,
        isMusicPlayingModel: Boolean,
    ) {
        isMusicPlaying = isMusicPlayingModel

        if (this.isMusicPlaying) {
            binding.bottomMusicLayout.visibility = View.VISIBLE
            bottomBindingView(activity, binding)

            if (isPlaying) {
                binding.imgPausePlayButton.setImageResource(R.drawable.ic_pause_button)

            } else {
                binding.imgPausePlayButton.setImageResource(R.drawable.ic_play_button)
            }

            binding.imgPausePlayButton.setOnClickListener {
                if (isPlaying) {
                    binding.imgPausePlayButton.setImageResource(R.drawable.ic_play_button)
                    player.pause()
                    isPlaying = !isPlaying
                } else {
                    binding.imgPausePlayButton.setImageResource(R.drawable.ic_pause_button)
                    player.play()
                    isPlaying = !isPlaying
                }
            }

            binding.imgNextButton.setOnClickListener {

                binding.imgPausePlayButton.setImageResource(R.drawable.ic_pause_button)
                player.play()
                isPlaying = !isPlaying
                if (player.isPlaying) {
                    player.pause()
                }
                binding.included.exoProgress.progress = 0
                musicPosition++
                if (musicPosition != musicList.size) {
                    musicPosition
                } else {
                    musicPosition = 0
                }
                bottomBindingView(activity, binding)
                playMusic(activity, binding, player)
            }

            binding.imgPreviousButton.setOnClickListener {
                binding.imgPausePlayButton.setImageResource(R.drawable.ic_pause_button)
                player.play()
                isPlaying = !isPlaying
                if (player.isPlaying) {
                    player.pause()
                }
                binding.included.exoProgress.progress = 0
                musicPosition--
                if (musicPosition < 0) {
                    musicPosition = musicList.size - 1
                } else {
                    musicPosition
                }
                bottomBindingView(activity, binding)
                playMusic(activity, binding, player)
            }

            binding.bottomMusicLayout.setOnClickListener {
                binding.included.customLayout.visibility = View.VISIBLE
                binding.musicListLayout.visibility = View.GONE
                if (isPlaying) {
                    binding.included.exoPlayPause.setImageResource(R.drawable.ic_pause_button)
                } else {
                    binding.included.exoPlayPause.setImageResource(R.drawable.ic_play_button)
                }
            }
        }
    }


    fun bottomBindingView(activity: Activity, binding: ActivityAudioBinding) {
        Glide.with(activity)
            .load("content://media/external/audio/albumart/${musicList[musicPosition].musicAlbum}")
            .into(binding.imgAudioFiles)
        binding.txtAudioFile.text = musicList[musicPosition].musicName
        binding.txtAudioArtist.text = musicList[musicPosition].musicArtist
    }

    fun playMusic(activity: Activity, binding: ActivityAudioBinding, player: ExoPlayer) {
        Glide.with(activity)
            .load("content://media/external/audio/albumart/${musicList[musicPosition].musicAlbum}")
            .into(binding.included.imgMusicThumbnail)
        binding.included.txtMusicTitle.text = musicList[musicPosition].musicName
        binding.included.txtArtistName.text = musicList[musicPosition].musicArtist
        binding.included.exoDuration.text =
            displayTime(musicList[musicPosition].musicDuration.toDouble())
        binding.included.exoProgress.max = musicList[musicPosition].musicDuration.toInt()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 1000)
                binding.included.exoPosition.text = displayTime(player.currentPosition.toDouble())
                binding.included.exoProgress.progress = player.currentPosition.toInt()
                if (displayTime(player.currentPosition.toDouble()).equals(displayTime(musicList[musicPosition].musicDuration.toDouble()))) {
                    if (player.isPlaying) {
                        player.pause()
                    }
                    binding.included.exoProgress.progress = 0
                    musicPosition++
                    if (musicPosition != musicList.size) {
                        musicPosition
                    } else {
                        musicPosition = 0
                    }
                    playMusic(activity, binding, player)
                }
            }
        }, 1000)

        binding.included.exoProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            var position = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                position = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress = position
                player.seekTo(position.toLong())
                binding.included.exoProgress.progress = position
            }
        })

        val mediaItem = MediaItem.fromUri(musicList[musicPosition].musicUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
}