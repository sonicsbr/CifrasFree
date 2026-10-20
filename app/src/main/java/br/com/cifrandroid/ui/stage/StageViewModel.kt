package br.com.cifrandroid.ui.stage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.cifrandroid.data.local.entity.SongEntity
import br.com.cifrandroid.data.repository.SongRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StageViewModel(
    private val repository: SongRepository,
    val initialSongId: Long,
    val setlistId: Long?
) : ViewModel() {

    // Lista de músicas: se veio de um setlist, carrega todas ordenadas; senão, apenas a música individual
    val playlistSongs: StateFlow<List<SongEntity>> = if (setlistId != null) {
        repository.getSongsForSetlist(setlistId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        repository.getSongById(initialSongId)
            .map { song -> if (song != null) listOf(song) else emptyList() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Modo de alto contraste: true = Fundo Preto com letras brancas, false = Fundo Branco com letras pretas
    val isHighContrastDark = MutableStateFlow(true)

    fun toggleContrastMode() {
        isHighContrastDark.value = !isHighContrastDark.value
    }

    fun updateTranspose(song: SongEntity, delta: Int) {
        val newTranspose = song.transposeSemitones + delta
        viewModelScope.launch {
            repository.updateTranspose(song.id, newTranspose)
        }
    }

    fun updateScrollSpeed(song: SongEntity, delta: Float) {
        val newSpeed = (Math.round((song.scrollSpeed + delta) * 10f) / 10f).coerceIn(0.2f, 5.0f)
        viewModelScope.launch {
            repository.updateScrollSpeed(song.id, newSpeed)
        }
    }

    fun updateFontSize(song: SongEntity, delta: Float) {
        val newSize = (song.fontSizeSp + delta).coerceIn(10.0f, 32.0f)
        viewModelScope.launch {
            repository.updateFontSize(song.id, newSize)
        }
    }

    class Factory(
        private val repository: SongRepository,
        private val initialSongId: Long,
        private val setlistId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StageViewModel(repository, initialSongId, setlistId) as T
        }
    }
}
