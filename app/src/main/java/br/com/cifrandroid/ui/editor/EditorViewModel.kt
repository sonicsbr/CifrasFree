package br.com.cifrandroid.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.cifrandroid.data.local.entity.SongEntity
import br.com.cifrandroid.data.repository.SongRepository
import br.com.cifrandroid.ui.web.CapturedDataHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    private val repository: SongRepository,
    val songId: Long,
    initialTitle: String
) : ViewModel() {

    val title = MutableStateFlow(initialTitle)
    val content = MutableStateFlow("")
    private val _isSaved = MutableStateFlow<Long?>(null)
    val isSaved = _isSaved.asStateFlow()

    init {
        if (CapturedDataHolder.capturedContent.isNotBlank()) {
            content.value = CapturedDataHolder.capturedContent
            if (title.value.isBlank()) {
                title.value = CapturedDataHolder.capturedTitle
            }
            CapturedDataHolder.clear()
        } else if (songId > 0) {
            viewModelScope.launch {
                val song = repository.getSongByIdSync(songId)
                if (song != null) {
                    title.value = song.title
                    content.value = song.content
                }
            }
        }
    }

    fun onTitleChanged(newTitle: String) {
        title.value = newTitle
    }

    fun onContentChanged(newContent: String) {
        content.value = newContent
    }

    fun pasteFromClipboard(clipboardText: String) {
        if (clipboardText.isNotBlank()) {
            content.value = clipboardText
        }
    }

    fun saveSong() {
        val t = title.value.trim().ifEmpty { "Sem Título" }
        val c = content.value.trim()
        if (c.isEmpty()) return

        viewModelScope.launch {
            val song = SongEntity(
                id = songId,
                title = t,
                content = c,
                updatedAt = System.currentTimeMillis()
            )
            val savedId = repository.saveSong(song)
            _isSaved.value = savedId
        }
    }

    class Factory(
        private val repository: SongRepository,
        private val songId: Long,
        private val initialTitle: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel(repository, songId, initialTitle) as T
        }
    }
}
