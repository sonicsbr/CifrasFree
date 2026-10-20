package br.com.cifrandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.cifrandroid.data.local.dao.SetlistDao
import br.com.cifrandroid.data.local.dao.SongDao
import br.com.cifrandroid.data.local.entity.SetlistEntity
import br.com.cifrandroid.data.local.entity.SetlistSongCrossRef
import br.com.cifrandroid.data.local.entity.SongEntity

@Database(
    entities = [
        SongEntity::class,
        SetlistEntity::class,
        SetlistSongCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CifrasDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun setlistDao(): SetlistDao

    companion object {
        @Volatile
        private var INSTANCE: CifrasDatabase? = null

        fun getInstance(context: Context): CifrasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CifrasDatabase::class.java,
                    "cifras_free.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
