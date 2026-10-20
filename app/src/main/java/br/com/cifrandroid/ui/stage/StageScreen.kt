package br.com.cifrandroid.ui.stage

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.cifrandroid.data.local.entity.SongEntity
import br.com.cifrandroid.music.parser.ChordParser
import br.com.cifrandroid.music.transposer.Transposer
import br.com.cifrandroid.ui.theme.ChordHighlightDark
import br.com.cifrandroid.ui.theme.ChordHighlightLight
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StageScreen(
    viewModel: StageViewModel,
    onBackToHome: () -> Unit,
    onEditSong: (songId: Long) -> Unit
) {
    val songs by viewModel.playlistSongs.collectAsStateWithLifecycle()
    val isHighContrastDark by viewModel.isHighContrastDark.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // WAKELOCK: Mantém a tela acesa enquanto o músico estiver no Modo Palco
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    if (songs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val initialIndex = remember(songs) {
        val idx = songs.indexOfFirst { it.id == viewModel.initialSongId }
        if (idx >= 0) idx else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { songs.size }
    )

    val currentSong = songs.getOrNull(pagerState.currentPage) ?: songs.first()
    var isAutoScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        isAutoScrolling = false
    }

    // Configurações de cores para acessibilidade visual (Preto/Branco)
    val bgColor = if (isHighContrastDark) Color.Black else Color.White
    val textColor = if (isHighContrastDark) Color.White else Color.Black
    val chordColor = if (isHighContrastDark) ChordHighlightDark else ChordHighlightLight

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentSong.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        if (songs.size > 1) {
                            Text(
                                text = "Música ${pagerState.currentPage + 1} de ${songs.size} (Deslize p/ navegar)",
                                fontSize = 12.sp,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar ao Setlist / Editar Ordem",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    // Botão explícito para voltar ao setlist
                    if (viewModel.setlistId != null) {
                        OutlinedButton(
                            onClick = onBackToHome,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Setlist", fontSize = 12.sp)
                        }
                    }

                    // Alternador de Contraste (Fundo Preto / Branco)
                    IconButton(onClick = viewModel::toggleContrastMode) {
                        Icon(
                            imageVector = if (isHighContrastDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Alternar Contraste",
                            tint = textColor
                        )
                    }

                    // Botão Editar
                    IconButton(onClick = { onEditSong(currentSong.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Cifra", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        bottomBar = {
            StageControlBar(
                song = currentSong,
                isHighContrastDark = isHighContrastDark,
                isAutoScrolling = isAutoScrolling,
                onToggleAutoScroll = { isAutoScrolling = !isAutoScrolling },
                onTransposeDelta = { delta -> viewModel.updateTranspose(currentSong, delta) },
                onScrollSpeedDelta = { delta -> viewModel.updateScrollSpeed(currentSong, delta) },
                onFontSizeDelta = { delta -> viewModel.updateFontSize(currentSong, delta) }
            )
        },
        containerColor = bgColor
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            val song = songs[page]
            SongPageView(
                song = song,
                textColor = textColor,
                chordColor = chordColor,
                isAutoScrolling = isAutoScrolling && (pagerState.currentPage == page),
                onAutoScrollToggle = { isAutoScrolling = it }
            )
        }
    }
}

@Composable
private fun SongPageView(
    song: SongEntity,
    textColor: Color,
    chordColor: Color,
    isAutoScrolling: Boolean,
    onAutoScrollToggle: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    // Velocidade base: 35dp/segundo na velocidade 1.0x
    val basePxPerSec = remember(density) { with(density) { 35.dp.toPx() } }
    val currentSpeed by rememberUpdatedState(song.scrollSpeed)

    LaunchedEffect(isAutoScrolling) {
        if (isAutoScrolling) {
            if (scrollState.maxValue == 0) {
                // Conteúdo cabe inteiro na tela, não há o que rolar
                onAutoScrollToggle(false)
                return@LaunchedEffect
            }
            try {
                scrollState.scroll(MutatePriority.Default) {
                    var lastTimeNanos = System.nanoTime()
                    while (isActive) {
                        delay(16L)
                        val now = System.nanoTime()
                        val dtSec = (now - lastTimeNanos).coerceAtLeast(0L) / 1_000_000_000f
                        lastTimeNanos = now

                        val pxToScroll = basePxPerSec * currentSpeed * dtSec
                        scrollBy(pxToScroll)

                        if (scrollState.value >= scrollState.maxValue && scrollState.maxValue > 0) {
                            onAutoScrollToggle(false)
                            break
                        }
                    }
                }
            } catch (e: CancellationException) {
                onAutoScrollToggle(false)
                throw e
            }
        }
    }

    // Transposição dos acordes
    val transposedText = remember(song.content, song.transposeSemitones) {
        Transposer.transposeSong(song.content, song.transposeSemitones)
    }

    // Marcação tipográfica e destaque dos acordes
    val annotatedContent = remember(transposedText, textColor, chordColor) {
        buildAnnotatedChordText(transposedText, textColor, chordColor)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = annotatedContent,
            fontFamily = FontFamily.Monospace,
            fontSize = song.fontSizeSp.sp,
            lineHeight = (song.fontSizeSp * 1.35f).sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StageControlBar(
    song: SongEntity,
    isHighContrastDark: Boolean,
    isAutoScrolling: Boolean,
    onToggleAutoScroll: () -> Unit,
    onTransposeDelta: (Int) -> Unit,
    onScrollSpeedDelta: (Float) -> Unit,
    onFontSizeDelta: (Float) -> Unit
) {
    val barBg = if (isHighContrastDark) Color(0xFF1E1E1E) else Color(0xFFF0F0F0)
    val contentColor = if (isHighContrastDark) Color.White else Color.Black

    Surface(
        color = barBg,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Controles de Transposição de Tom
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onTransposeDelta(-1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Descer Meio Tom", tint = contentColor)
                }
                Text(
                    text = if (song.transposeSemitones > 0) "+${song.transposeSemitones}" else "${song.transposeSemitones}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = contentColor
                )
                IconButton(onClick = { onTransposeDelta(1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Subir Meio Tom", tint = contentColor)
                }
            }

            // Auto-Scroll Play/Pause e Velocidade
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleAutoScroll,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isAutoScrolling) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = if (isAutoScrolling) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Rolagem Automática",
                        tint = if (isAutoScrolling) Color.Black else contentColor
                    )
                }
                IconButton(onClick = { onScrollSpeedDelta(-0.2f) }, enabled = song.scrollSpeed > 0.4f) {
                    Icon(Icons.Default.FastRewind, contentDescription = "Diminuir Velocidade", tint = contentColor)
                }
                Text(
                    text = String.format("%.1fx", song.scrollSpeed),
                    fontSize = 12.sp,
                    color = contentColor
                )
                IconButton(onClick = { onScrollSpeedDelta(0.2f) }, enabled = song.scrollSpeed < 4.8f) {
                    Icon(Icons.Default.FastForward, contentDescription = "Aumentar Velocidade", tint = contentColor)
                }
            }

            // Tamanho da Fonte (+ / -)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onFontSizeDelta(-1.0f) }, enabled = song.fontSizeSp > 11f) {
                    Text("A-", fontWeight = FontWeight.Bold, color = contentColor, fontSize = 12.sp)
                }
                IconButton(onClick = { onFontSizeDelta(1.0f) }, enabled = song.fontSizeSp < 31f) {
                    Text("A+", fontWeight = FontWeight.Bold, color = contentColor, fontSize = 16.sp)
                }
            }
        }
    }
}

private fun buildAnnotatedChordText(
    fullText: String,
    textColor: Color,
    chordColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        val lines = fullText.lines()
        for (i in lines.indices) {
            val line = lines[i]
            if (ChordParser.isChordLine(line)) {
                val tokens = ChordParser.findChordsInLine(line)
                var lastIndex = 0
                for (token in tokens) {
                    if (token.startIndex > lastIndex) {
                        withStyle(SpanStyle(color = textColor)) {
                            append(line.substring(lastIndex, token.startIndex))
                        }
                    }
                    withStyle(SpanStyle(color = chordColor, fontWeight = FontWeight.Bold)) {
                        append(token.rawText)
                    }
                    lastIndex = token.endIndex
                }
                if (lastIndex < line.length) {
                    withStyle(SpanStyle(color = textColor)) {
                        append(line.substring(lastIndex))
                    }
                }
            } else {
                withStyle(SpanStyle(color = textColor)) {
                    append(line)
                }
            }

            if (i < lines.size - 1) {
                append("\n")
            }
        }
    }
}
