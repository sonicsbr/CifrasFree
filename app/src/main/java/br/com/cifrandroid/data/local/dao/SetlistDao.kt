package br.com.cifrandroid.data.local.dao

import androidx.room.*
import br.com.cifrandroid.data.local.entity.SetlistEntity
import br.com.cifrandroid.data.local.entity.SetlistSongCrossRef
import br.com.cifrandroid.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetlistDao {

    @Query("SELECT * FROM setlists ORDER BY name COLLATE NOCASE ASC")
    fun getAllSetlists(): Flow<List<SetlistEntity>>

    @Query("SELECT * FROM setlists WHERE id = :id")
    fun getSetlistById(id: Long): Flow<SetlistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetlist(setlist: SetlistEntity): Long

    @Update
    suspend fun updateSetlist(setlist: SetlistEntity)

    @Delete
    suspend fun deleteSetlist(setlist: SetlistEntity)

    @Query("DELETE FROM setlists WHERE id = :id")
    suspend fun deleteSetlistById(id: Long)

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN setlist_song_cross_ref ref ON s.id = ref.songId
        WHERE ref.setlistId = :setlistId
        ORDER BY ref.positionOrder ASC
    """)
    fun getSongsForSetlist(setlistId: Long): Flow<List<SongEntity>>

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN setlist_song_cross_ref ref ON s.id = ref.songId
        WHERE ref.setlistId = :setlistId
        ORDER BY ref.positionOrder ASC
    """)
    suspend fun getSongsForSetlistSync(setlistId: Long): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToSetlist(crossRef: SetlistSongCrossRef)

    @Query("DELETE FROM setlist_song_cross_ref WHERE setlistId = :setlistId AND songId = :songId")
    suspend fun removeSongFromSetlist(setlistId: Long, songId: Long)

    @Query("SELECT MAX(positionOrder) FROM setlist_song_cross_ref WHERE setlistId = :setlistId")
    suspend fun getMaxPositionOrder(setlistId: Long): Int?

    @Query("UPDATE setlist_song_cross_ref SET positionOrder = :positionOrder WHERE setlistId = :setlistId AND songId = :songId")
    suspend fun updateSongPosition(setlistId: Long, songId: Long, positionOrder: Int)

    @Transaction
    suspend fun updateSetlistSongOrder(setlistId: Long, orderedSongIds: List<Long>) {
        orderedSongIds.forEachIndexed { index, songId ->
            updateSongPosition(setlistId, songId, index)
        }
    }
}
