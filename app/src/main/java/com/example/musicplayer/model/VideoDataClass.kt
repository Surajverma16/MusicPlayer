package com.example.musicplayer.model

import android.os.Parcel
import android.os.Parcelable

data class VideoDataClass(
    val id: String?,
    val title: String?,
    val displayName: String?,
    val size: String?,
    val duration: String?,
    val path: String?,
    val dateAdded: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(displayName)
        parcel.writeString(size)
        parcel.writeString(duration)
        parcel.writeString(path)
        parcel.writeString(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoDataClass> {
        override fun createFromParcel(parcel: Parcel): VideoDataClass {
            return VideoDataClass(parcel)
        }

        override fun newArray(size: Int): Array<VideoDataClass?> {
            return arrayOfNulls(size)
        }
    }

}