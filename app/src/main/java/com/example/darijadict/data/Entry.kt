package com.example.darijadict.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// Remove the 'saved' field completely since we're using lists now
@Parcelize
@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val arabicScript: String,
    val meaning: String,
    val pos: String? = null,
    val plural: String? = null,
    val present: String? = null,
    val fs: String? = null,
    val mp: String? = null,
    val fp: String? = null,
    val uses: String? = null,
    val example: String? = null,
    val pronunciation: String? = null,
    val usesPronunciation: String? = null,
    val saved: Boolean = false // Make sure this exists
) : Parcelable