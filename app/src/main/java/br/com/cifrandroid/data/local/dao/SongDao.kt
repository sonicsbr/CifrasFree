package br.com.cifrandroid.data.local.dao

import androidx.room.*
import br.com.cifrandroid.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM songs ORDER BY title COLLATE NOCASE ASC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("""
        SELECT * FROM songs 
        WHERE title LIKE '%' || :query || '%' 
           OR content LIKE '%' || :query || '%'
        ORDER BY title COLLATE NOCASE ASC
    """)
    fun searchSongs(query: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSongById(id: Long): Flow<SongEntity?>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongByIdSync(id: Long): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>): List<Long>

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("UPDATE songs SET transposeSemitones = :transposeSemitones, updatedAt = :updatedAt WHERE id = :songId")
    suspend fun updateTranspose(songId: Long, transposeSemitones: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE songs SET scrollSpeed = :scrollSpeed, updatedAt = :updatedAt WHERE id = :songId")
    suspend fun updateScrollSpeed(songId: Long, scrollSpeed: Float, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE songs SET fontSizeSp = :fontSizeSp, updatedAt = :updatedAt WHERE id = :songId")
    suspend fun updateFontSize(songId: Long, fontSizeSp: Float, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteSong(song: SongEntity)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSongById(id: Long)
}
