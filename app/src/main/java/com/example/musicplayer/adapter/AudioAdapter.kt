package com.example.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.model.MusicDataClass

class AudioAdapter(
    val audioFiles: ArrayList<MusicDataClass>,
    val player: ExoPlayer,
    val context: Context,
    val onCLicked: clickedMusic
) : RecyclerView.Adapter<AudioAdapter.audioViewHolder>() {
    class audioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicName = itemView.findViewById<TextView>(R.id.txtAudioFile)
        val musicImage = itemView.findViewById<ImageView>(R.id.imgAudioFiles)
        val artist = itemView.findViewById<TextView>(R.id.txtAudioArtist)
        val layout = itemView.findViewById<ConstraintLayout>(R.id.audio_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): audioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_music_list, parent, false)
        return audioViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return audioFiles.size
    }

    override fun onBindViewHolder(holder: audioViewHolder, position: Int) {
        holder.musicName.text = audioFiles[position].musicName
        holder.artist.text = audioFiles[position].musicArtist
        Glide.with(context)
            .load("content://media/external/audio/albumart/${audioFiles[position].musicAlbum}")
            .into(holder.musicImage)

        holder.layout.setOnClickListener {
            if(player.isPlaying){
                player.pause()
            }
            player.prepare()
            onCLicked.playMusic(audioFiles, position)
        }
    }

    interface clickedMusic {
        fun playMusic(audioFiles: ArrayList<MusicDataClass>, position: Int)
    }
}