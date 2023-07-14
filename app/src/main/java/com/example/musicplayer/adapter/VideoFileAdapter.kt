package com.example.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.VideoPlay
import com.example.musicplayer.model.VideoDataClass
import java.io.File
import java.util.concurrent.TimeUnit

class VideoFileAdapter(val context: Context, val videoList : ArrayList<VideoDataClass>) : RecyclerView.Adapter<VideoFileAdapter.videoFileViewHolder>() {
    class videoFileViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val fileImage = itemView.findViewById<ImageView>(R.id.imgVideoFiles)
        val fileText = itemView.findViewById<TextView>(R.id.txtVideoFile)
        val fileDuration = itemView.findViewById<TextView>(R.id.txtVideoDuration)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): videoFileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_video_list, parent, false)
        return videoFileViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: videoFileViewHolder, position: Int) {
        Glide.with(context).load(File(videoList[position].path!!)).into(holder.fileImage)
        holder.fileText.text = videoList[position].displayName
        holder.fileDuration.text = displayTime(videoList[position].duration!!.toDouble())

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList("data", videoList)
            context.startActivity(
                Intent(context, VideoPlay::class.java)
                    .putExtra("position", position)
                    .putExtra("displayName", videoList[position].displayName)
                    .putExtra("bundle", bundle)
            )
        }

    }
    private fun displayTime(millisecond: Double): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisecond.toLong())
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond.toLong()) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millisecond.toLong()))
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond.toLong()) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millisecond.toLong()))

        Log.i("hours", hours.toString())
        Log.i("minutes", minutes.toString())
        Log.i("seconds", seconds.toString())

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

}