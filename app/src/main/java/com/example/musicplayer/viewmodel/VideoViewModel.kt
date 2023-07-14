package com.example.musicplayer.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.*
import androidx.media3.exoplayer.video.VideoFrameMetadataListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.adapter.VideoFileAdapter
import com.example.musicplayer.adapter.VideoFolderAdapter
import com.example.musicplayer.databinding.ActivityVideoFileBinding
import com.example.musicplayer.databinding.ActivityVideoFolderBinding
import com.example.musicplayer.databinding.ActivityVideoPlayBinding
import com.example.musicplayer.model.VideoDataClass
import com.google.common.net.HttpHeaders
import java.io.File

@UnstableApi
class VideoViewModel : ViewModel() {

    private var videoFolderList = arrayListOf<String>()
    private var videoFileList = arrayListOf<VideoDataClass>()
    private lateinit var videoAdapter: VideoFolderAdapter
    val PERMISSION_REQUEST_CODE = 123

    fun permission(
        activity: Activity,
        binding: ActivityVideoFolderBinding,
        contentResolver: ContentResolver,
    ) {
        if (checkPermission(activity)) {
            fetchVideos(activity, binding, contentResolver)
        } else {
            requestPermission(activity)
        }
    }

    private fun checkPermission(activity: Activity): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun fetchVideos(
        activity: Activity,
        binding: ActivityVideoFolderBinding,
        contentResolver: ContentResolver,
    ) {
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(videoUri, projection, null, null, null)

        cursor?.let {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            while (it.moveToNext()) {
                val videoPath = it.getString(columnIndex)
                if (!videoFolderList.contains(File(videoPath).parentFile!!.name)) {
                    videoFolderList.add(File(videoPath).parentFile!!.name)
                }
            }
            cursor.close()
        }
        videoAdapter = VideoFolderAdapter(activity, videoFolderList)
        binding.videoFolderRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.videoFolderRecyclerView.adapter = videoAdapter
    }

    fun showVideoFiles(
        folderName: String,
        activity: Activity,
        binding: ActivityVideoFileBinding,
        contentResolver: ContentResolver,
    ) {
        videoFileList = fetchMedia(folderName, contentResolver)

        binding.videoFileRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.videoFileRecyclerView.adapter = VideoFileAdapter(activity, videoFileList)
    }

    @SuppressLint("Range")
    fun fetchMedia(
        folderName: String,
        contentResolver: ContentResolver,
    ): ArrayList<VideoDataClass> {
        val videoFiles = arrayListOf<VideoDataClass>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Video.Media.DATA} LIKE ?"
        val cursor = contentResolver.query(uri, null, selection, arrayOf("%/$folderName/%"), null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val displayName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val duration =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val dateAdded =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))

                val mediaData =
                    VideoDataClass(id, title, displayName, size, duration, path, dateAdded)
                videoFiles.add(mediaData)
            }
        }
        return videoFiles
    }

    fun playVideo(
        activity: Activity,
        player: ExoPlayer,
        videoPlayList: ArrayList<VideoDataClass>,
        position: Int,
    ) {
        val mediaItem = MediaItem.fromUri(videoPlayList[position].path!!)
        val dataSourceFactory = DefaultDataSourceFactory(activity, HttpHeaders.USER_AGENT)
        val concatenatingMediaSource = ConcatenatingMediaSource()

        var currentOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

         val videoListener =
            (VideoFrameMetadataListener { presentationTimeUs, releaseTimeNs, format, mediaFormat ->
                val newOrientation = if (format.width > format.height) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                if (newOrientation != currentOrientation) {
                    currentOrientation = newOrientation
                    activity.requestedOrientation = currentOrientation
                }
            })

        for (i in 0..videoPlayList.size) {
            val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSourceFactory)
        }

        concatenatingMediaSource.addEventListener(Handler(), object : MediaSourceEventListener {
            override fun onLoadCompleted(
                windowIndex: Int,
                mediaPeriodId: MediaSource.MediaPeriodId?,
                loadEventInfo: LoadEventInfo,
                mediaLoadData: MediaLoadData,
            ) {
                super.onLoadCompleted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
                if (mediaLoadData.trackFormat != null) {
                    activity.requestedOrientation =
                        if (mediaLoadData.trackFormat!!.width > mediaLoadData.trackFormat!!.height) {
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                }
            }
        })

        player.apply {
            prepare(concatenatingMediaSource)
            play()
            playWhenReady = true
            seekTo(position, C.TIME_UNSET)
            setVideoFrameMetadataListener(videoListener)
        }

    }

    fun setFullScreen(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}