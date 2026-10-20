package br.com.cifrandroid.data.repository

import android.content.Context
import android.net.Uri
import br.com.cifrandroid.data.importer.ImportProgress
import br.com.cifrandroid.data.importer.SongImporter
import br.com.cifrandroid.data.local.CifrasDatabase
import br.com.cifrandroid.data.local.entity.SetlistEntity
import br.com.cifrandroid.data.local.entity.SetlistSongCrossRef
import br.com.cifrandroid.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

class SongRepository(
    private val database: CifrasDatabase,
    private val importer: SongImporter
) {
    private val songDao = database.songDao()
    private val setlistDao = database.setlistDao()

    // --- Músicas ---
    fun getAllSongs(): Flow<List<SongEntity>> = songDao.getAllSongs()

    fun searchSongs(query: String): Flow<List<SongEntity>> = songDao.searchSongs(query)

    fun getSongById(id: Long): Flow<SongEntity?> = songDao.getSongById(id)

    suspend fun getSongByIdSync(id: Long): SongEntity? = songDao.getSongByIdSync(id)

    suspend fun saveSong(song: SongEntity): Long {
        return if (song.id == 0L) {
            songDao.insertSong(song)
        } else {
            songDao.updateSong(song)
            song.id
        }
    }

    suspend fun updateTranspose(songId: Long, semitones: Int) {
        songDao.updateTranspose(songId, semitones)
    }

    suspend fun updateScrollSpeed(songId: Long, speed: Float) {
        songDao.updateScrollSpeed(songId, speed)
    }

    suspend fun updateFontSize(songId: Long, fontSizeSp: Float) {
        songDao.updateFontSize(songId, fontSizeSp)
    }

    suspend fun deleteSong(song: SongEntity) {
        songDao.deleteSong(song)
    }

    suspend fun deleteSongById(id: Long) {
        songDao.deleteSongById(id)
    }

    // --- Setlists ---
    fun getAllSetlists(): Flow<List<SetlistEntity>> = setlistDao.getAllSetlists()

    fun getSetlistById(id: Long): Flow<SetlistEntity?> = setlistDao.getSetlistById(id)

    suspend fun createSetlist(name: String): Long {
        return setlistDao.insertSetlist(SetlistEntity(name = name))
    }

    suspend fun updateSetlist(setlist: SetlistEntity) {
        setlistDao.updateSetlist(setlist)
    }

    suspend fun deleteSetlist(setlist: SetlistEntity) {
        setlistDao.deleteSetlist(setlist)
    }

    fun getSongsForSetlist(setlistId: Long): Flow<List<SongEntity>> =
        setlistDao.getSongsForSetlist(setlistId)

    suspend fun getSongsForSetlistSync(setlistId: Long): List<SongEntity> =
        setlistDao.getSongsForSetlistSync(setlistId)

    suspend fun addSongToSetlist(setlistId: Long, songId: Long) {
        val nextOrder = (setlistDao.getMaxPositionOrder(setlistId) ?: -1) + 1
        setlistDao.addSongToSetlist(
            SetlistSongCrossRef(
                setlistId = setlistId,
                songId = songId,
                positionOrder = nextOrder
            )
        )
    }

    suspend fun removeSongFromSetlist(setlistId: Long, songId: Long) {
        setlistDao.removeSongFromSetlist(setlistId, songId)
    }

    suspend fun updateSetlistSongOrder(setlistId: Long, orderedSongIds: List<Long>) {
        setlistDao.updateSetlistSongOrder(setlistId, orderedSongIds)
    }

    // --- Importação SAF ---
    suspend fun importSingleTextFile(uri: Uri): Result<SongEntity> =
        importer.importSingleTextFile(uri)

    fun importZipFile(uri: Uri): Flow<ImportProgress> =
        importer.importZipFile(uri)

    companion object {
        @Volatile
        private var INSTANCE: SongRepository? = null

        fun getInstance(context: Context): SongRepository {
            return INSTANCE ?: synchronized(this) {
                val db = CifrasDatabase.getInstance(context)
                val importer = SongImporter(context.contentResolver, db.songDao())
                val instance = SongRepository(db, importer)
                INSTANCE = instance
                instance
            }
        }
    }
}
