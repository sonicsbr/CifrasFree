package br.com.cifrandroid.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.cifrandroid.data.importer.ImportProgress
import br.com.cifrandroid.data.local.entity.SetlistEntity
import br.com.cifrandroid.data.local.entity.SongEntity
import br.com.cifrandroid.data.repository.SongRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class HomeTab {
    ALL_SONGS,
    SETLISTS
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: SongRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val activeTab = MutableStateFlow(HomeTab.ALL_SONGS)
    val selectedSetlist = MutableStateFlow<SetlistEntity?>(null)
    val importProgress = MutableStateFlow<ImportProgress?>(null)

    // Lista de todas as músicas filtradas por busca com debounce
    val songs: StateFlow<List<SongEntity>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllSongs()
            } else {
                repository.searchSongs(query.trim())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Todos os setlists
    val setlists: StateFlow<List<SetlistEntity>> = repository.getAllSetlists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Músicas do setlist selecionado
    val setlistSongs: StateFlow<List<SongEntity>> = selectedSetlist
        .flatMapLatest { setlist ->
            if (setlist == null) flowOf(emptyList())
            else repository.getSongsForSetlist(setlist.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onTabSelected(tab: HomeTab) {
        activeTab.value = tab
        if (tab == HomeTab.ALL_SONGS) {
            selectedSetlist.value = null
        }
    }

    fun selectSetlist(setlist: SetlistEntity?) {
        selectedSetlist.value = setlist
        if (setlist != null) {
            searchQuery.value = ""
        }
    }

    fun createSetlist(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createSetlist(name.trim())
        }
    }

    fun deleteSetlist(setlist: SetlistEntity) {
        viewModelScope.launch {
            if (selectedSetlist.value?.id == setlist.id) {
                selectedSetlist.value = null
            }
            repository.deleteSetlist(setlist)
        }
    }

    fun addSongToCurrentSetlist(songId: Long) {
        val current = selectedSetlist.value ?: return
        viewModelScope.launch {
            repository.addSongToSetlist(current.id, songId)
        }
    }

    fun removeSongFromCurrentSetlist(songId: Long) {
        val current = selectedSetlist.value ?: return
        viewModelScope.launch {
            repository.removeSongFromSetlist(current.id, songId)
        }
    }

    fun reorderSetlistSongs(fromIndex: Int, toIndex: Int) {
        val current = selectedSetlist.value ?: return
        val currentList = setlistSongs.value.toMutableList()
        if (fromIndex !in currentList.indices || toIndex !in currentList.indices) return

        val item = currentList.removeAt(fromIndex)
        currentList.add(toIndex, item)

        viewModelScope.launch {
            repository.updateSetlistSongOrder(current.id, currentList.map { it.id })
        }
    }

    fun deleteSong(song: SongEntity) {
        viewModelScope.launch {
            repository.deleteSong(song)
        }
    }

    // --- Importação SAF ---
    fun importSingleTextFile(uri: Uri, onComplete: (SongEntity?) -> Unit) {
        viewModelScope.launch {
            val result = repository.importSingleTextFile(uri)
            onComplete(result.getOrNull())
        }
    }

    fun importZipFile(uri: Uri) {
        viewModelScope.launch {
            repository.importZipFile(uri).collect { progress ->
                importProgress.value = progress
            }
        }
    }

    fun dismissImportProgress() {
        importProgress.value = null
    }

    class Factory(private val repository: SongRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
