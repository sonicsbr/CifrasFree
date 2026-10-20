package br.com.cifrandroid

import android.app.Application
import br.com.cifrandroid.data.repository.SongRepository

class CifrasApplication : Application() {

    lateinit var repository: SongRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = SongRepository.getInstance(this)
    }
}
