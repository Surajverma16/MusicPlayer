package com.example.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.VideoFileActivity
import com.example.musicplayer.VideoFolderActivity

class VideoFolderAdapter(val context: Context, val videoList: ArrayList<String>) :
    RecyclerView.Adapter<VideoFolderAdapter.myVideoViewHolder>() {
    class myVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName = itemView.findViewById<TextView>(R.id.txtFolderName)
        val layout = itemView.findViewById<ConstraintLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myVideoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.folder_list_layout, parent, false)
        return myVideoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: myVideoViewHolder, position: Int) {
        holder.folderName.text = videoList[position]
        holder.layout.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    VideoFileActivity::class.java
                ).putExtra("folderName", videoList[position])
            )
        }
    }
}