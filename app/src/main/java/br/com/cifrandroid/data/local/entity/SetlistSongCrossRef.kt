package br.com.cifrandroid.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "setlist_song_cross_ref",
    primaryKeys = ["setlistId", "songId"],
    foreignKeys = [
        ForeignKey(
            entity = SetlistEntity::class,
            parentColumns = ["id"],
            childColumns = ["setlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("setlistId"), Index("songId")]
)
data class SetlistSongCrossRef(
    val setlistId: Long,
    val songId: Long,
    val positionOrder: Int
)
