package br.com.cifrandroid.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.cifrandroid.data.local.entity.SetlistEntity
import br.com.cifrandroid.data.local.entity.SongEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSongClick: (songId: Long, setlistId: Long?) -> Unit,
    onCreateManualSong: () -> Unit,
    onOpenWebBrowser: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val setlists by viewModel.setlists.collectAsStateWithLifecycle()
    val setlistSongs by viewModel.setlistSongs.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val selectedSetlist by viewModel.selectedSetlist.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val importProgress by viewModel.importProgress.collectAsStateWithLifecycle()

    var showFabMenu by remember { mutableStateOf(false) }
    var showNewSetlistDialog by remember { mutableStateOf(false) }
    var showAddSongsToSetlistDialog by remember { mutableStateOf(false) }

    // Launchers do Storage Access Framework (SAF)
    val singleTextLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importSingleTextFile(it) { song ->
            if (song != null) onSongClick(song.id, null)
        } }
    }

    val zipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importZipFile(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedSetlist != null) {
                        Text(
                            text = selectedSetlist!!.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            placeholder = { Text("Buscar título ou trecho da letra...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpar")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (selectedSetlist != null) {
                        IconButton(onClick = { viewModel.selectSetlist(null) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar aos Setlists")
                        }
                    }
                },
                actions = {
                    if (selectedSetlist != null) {
                        IconButton(onClick = { showAddSongsToSetlistDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar Músicas")
                        }
                        IconButton(onClick = {
                            if (setlistSongs.isNotEmpty()) {
                                onSongClick(setlistSongs.first().id, selectedSetlist!!.id)
                            }
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Tocar Repertório")
                        }
                    } else {
                        IconButton(onClick = onOpenAbout) {
                            Icon(Icons.Default.Info, contentDescription = "Sobre o CifrasFree")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedSetlist == null) {
                Column(horizontalAlignment = Alignment.End) {
                    if (showFabMenu) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onCreateManualSong()
                            },
                            icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            text = { Text("Criar Manualmente") },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onOpenWebBrowser()
                            },
                            icon = { Icon(Icons.Default.Language, contentDescription = null) },
                            text = { Text("Buscar na Web") },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                singleTextLauncher.launch("text/*")
                            },
                            icon = { Icon(Icons.Default.Description, contentDescription = null) },
                            text = { Text("Importar .txt Único") },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                zipLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed"))
                            },
                            icon = { Icon(Icons.Default.Archive, contentDescription = null) },
                            text = { Text("Importar Lote .zip") },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (activeTab == HomeTab.SETLISTS) {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    showFabMenu = false
                                    showNewSetlistDialog = true
                                },
                                icon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null) },
                                text = { Text("Novo Setlist") },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = { showFabMenu = !showFabMenu }
                    ) {
                        Icon(
                            imageVector = if (showFabMenu) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Menu de Adição"
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (selectedSetlist == null) {
                TabRow(selectedTabIndex = activeTab.ordinal) {
                    Tab(
                        selected = activeTab == HomeTab.ALL_SONGS,
                        onClick = { viewModel.onTabSelected(HomeTab.ALL_SONGS) },
                        text = { Text("Todas as Músicas (${songs.size})") }
                    )
                    Tab(
                        selected = activeTab == HomeTab.SETLISTS,
                        onClick = { viewModel.onTabSelected(HomeTab.SETLISTS) },
                        text = { Text("Meus Setlists (${setlists.size})") }
                    )
                }

                when (activeTab) {
                    HomeTab.ALL_SONGS -> {
                        SongList(
                            songs = songs,
                            onSongClick = { onSongClick(it.id, null) },
                            onDeleteSong = viewModel::deleteSong
                        )
                    }
                    HomeTab.SETLISTS -> {
                        SetlistList(
                            setlists = setlists,
                            onSetlistClick = viewModel::selectSetlist,
                            onDeleteSetlist = viewModel::deleteSetlist
                        )
                    }
                }
            } else {
                // Visualização detalhada do Setlist selecionado
                SetlistDetailList(
                    songs = setlistSongs,
                    onSongClick = { song -> onSongClick(song.id, selectedSetlist!!.id) },
                    onMoveUp = { idx -> viewModel.reorderSetlistSongs(idx, idx - 1) },
                    onMoveDown = { idx -> viewModel.reorderSetlistSongs(idx, idx + 1) },
                    onRemoveSong = { songId -> viewModel.removeSongFromCurrentSetlist(songId) }
                )
            }
        }
    }

    // Modal de criação de Setlist
    if (showNewSetlistDialog) {
        var setlistName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewSetlistDialog = false },
            title = { Text("Novo Setlist") },
            text = {
                OutlinedTextField(
                    value = setlistName,
                    onValueChange = { setlistName = it },
                    label = { Text("Nome do Repertório") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (setlistName.isNotBlank()) {
                            viewModel.createSetlist(setlistName)
                            showNewSetlistDialog = false
                        }
                    }
                ) {
                    Text("Criar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewSetlistDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal para adicionar músicas ao setlist atual
    if (showAddSongsToSetlistDialog && selectedSetlist != null) {
        AlertDialog(
            onDismissRequest = { showAddSongsToSetlistDialog = false },
            title = { Text("Adicionar Música ao Setlist") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    val currentSongIds = setlistSongs.map { it.id }.toSet()
                    val available = songs.filterNot { it.id in currentSongIds }

                    if (available.isEmpty()) {
                        item {
                            Text("Todas as músicas já foram adicionadas a este setlist.")
                        }
                    } else {
                        items(available.size) { idx ->
                            val s = available[idx]
                            ListItem(
                                headlineContent = { Text(s.title) },
                                modifier = Modifier.clickable {
                                    viewModel.addSongToCurrentSetlist(s.id)
                                    showAddSongsToSetlistDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddSongsToSetlistDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }

    // Modal de progresso de importação ZIP
    importProgress?.let { progress ->
        AlertDialog(
            onDismissRequest = { if (progress.isComplete) viewModel.dismissImportProgress() },
            title = { Text(if (progress.isComplete) "Importação Finalizada" else "Importando Cifras...") },
            text = {
                Column {
                    if (!progress.isComplete) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Processando: ${progress.currentFileName}")
                        Text("Músicas extraídas: ${progress.current}")
                    } else {
                        if (progress.errorMessage != null) {
                            Text("Ocorreu um erro: ${progress.errorMessage}", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Sucesso! ${progress.totalImported} músicas importadas para o seu banco de dados.")
                        }
                    }
                }
            },
            confirmButton = {
                if (progress.isComplete) {
                    TextButton(onClick = viewModel::dismissImportProgress) {
                        Text("OK")
                    }
                }
            }
        )
    }
}

@Composable
private fun SongList(
    songs: List<SongEntity>,
    onSongClick: (SongEntity) -> Unit,
    onDeleteSong: (SongEntity) -> Unit
) {
    if (songs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhuma música encontrada.\nUse o botão '+' para adicionar.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(songs.size, key = { songs[it].id }) { index ->
                val song = songs[index]
                var showDeleteConfirm by remember { mutableStateOf(false) }

                ListItem(
                    headlineContent = {
                        Text(song.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    supportingContent = {
                        Text(
                            text = song.content.lines().firstOrNull { it.isNotBlank() } ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Gray
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir")
                        }
                    },
                    modifier = Modifier.clickable { onSongClick(song) }
                )
                HorizontalDivider()

                if (showDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = { Text("Excluir Música") },
                        text = { Text("Deseja realmente excluir '${song.title}'?") },
                        confirmButton = {
                            TextButton(onClick = {
                                onDeleteSong(song)
                                showDeleteConfirm = false
                            }) {
                                Text("Excluir", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SetlistList(
    setlists: List<SetlistEntity>,
    onSetlistClick: (SetlistEntity) -> Unit,
    onDeleteSetlist: (SetlistEntity) -> Unit
) {
    if (setlists.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum setlist criado ainda.\nToque no '+' para criar um repertório.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(setlists.size, key = { setlists[it].id }) { index ->
                val setlist = setlists[index]
                var showDeleteConfirm by remember { mutableStateOf(false) }

                ListItem(
                    leadingContent = { Icon(Icons.Default.QueueMusic, contentDescription = null) },
                    headlineContent = {
                        Text(setlist.name, fontWeight = FontWeight.SemiBold)
                    },
                    trailingContent = {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir")
                        }
                    },
                    modifier = Modifier.clickable { onSetlistClick(setlist) }
                )
                HorizontalDivider()

                if (showDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = { Text("Excluir Setlist") },
                        text = { Text("Deseja excluir o setlist '${setlist.name}'? (As músicas não serão apagadas).") },
                        confirmButton = {
                            TextButton(onClick = {
                                onDeleteSetlist(setlist)
                                showDeleteConfirm = false
                            }) {
                                Text("Excluir", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SetlistDetailList(
    songs: List<SongEntity>,
    onSongClick: (SongEntity) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemoveSong: (Long) -> Unit
) {
    if (songs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Este setlist está vazio.\nToque em '+' no topo para adicionar músicas.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                ListItem(
                    leadingContent = {
                        Text(
                            text = "${index + 1}.",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(28.dp)
                        )
                    },
                    headlineContent = {
                        Text(song.title, fontWeight = FontWeight.Medium)
                    },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onMoveUp(index) },
                                enabled = index > 0
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Mover para cima")
                            }
                            IconButton(
                                onClick = { onMoveDown(index) },
                                enabled = index < songs.size - 1
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Mover para baixo")
                            }
                            IconButton(onClick = { onRemoveSong(song.id) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remover do Setlist")
                            }
                        }
                    },
                    modifier = Modifier.clickable { onSongClick(song) }
                )
                HorizontalDivider()
            }
        }
    }
}
