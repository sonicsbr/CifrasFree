package br.com.cifrandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val transposeSemitones: Int = 0,
    val scrollSpeed: Float = 1.0f,
    val fontSizeSp: Float = 16.0f,
    val updatedAt: Long = System.currentTimeMillis()
)
