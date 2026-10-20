package br.com.cifrandroid.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit,
    onSaveSuccess: (savedSongId: Long) -> Unit
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(isSaved) {
        isSaved?.let { savedId ->
            onSaveSuccess(savedId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.songId > 0) "Editar Cifra" else "Nova Cifra") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::saveSong,
                        enabled = content.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Salvar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Título da Música") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Letra e Cifras:",
                    style = MaterialTheme.typography.titleSmall
                )
                TextButton(
                    onClick = {
                        val clip = clipboardManager.getText()?.text
                        if (!clip.isNullOrBlank()) {
                            viewModel.pasteFromClipboard(clip)
                        }
                    }
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Colar da Área de Transferência")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = content,
                onValueChange = viewModel::onContentChanged,
                placeholder = { Text("Cole ou digite a letra e as cifras aqui...") },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
