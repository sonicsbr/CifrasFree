package br.com.cifrandroid.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre o CifrasFree") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Cabeçalho centralizado
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CifrasFree",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Versão 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Por músicos, para o palco.",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Card de Apresentação / Manifesto
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "O CifrasFree nasceu de uma necessidade real de quem toca: ter um leitor de cifras e gerenciador de repertório absolutamente confiável, rápido e livre de distrações. Projetado com arquitetura moderna para o Android atual, ele garante que suas cifras, seus tons personalizados e a ordem do seu setlist estejam sempre prontos, 100% offline e legíveis sob qualquer iluminação.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Card de Destaques
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Destaques do App",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HighlightItem(
                        icon = Icons.Default.BrightnessHigh,
                        title = "Modo Palco",
                        description = "Tela sempre ativa, auto-scroll e alto contraste."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HighlightItem(
                        icon = Icons.Default.Tune,
                        title = "Memória Musical",
                        description = "Salva automaticamente seu tom e velocidade preferidos por música."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HighlightItem(
                        icon = Icons.Default.Reorder,
                        title = "Setlists Dinâmicos",
                        description = "Organização por drag-and-drop e transição lateral rápida (swipe)."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HighlightItem(
                        icon = Icons.Default.FolderZip,
                        title = "Liberdade de Acervo",
                        description = "Captura web embutida e importação em lote (.txt e .zip)."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Card de Créditos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Créditos & Engenharia",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Idealização, Arquitetura e Desenvolvimento: Leo Vaz",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Motor musical de reconhecimento de acordes inspirado no projeto open-source Chord Reader 2 (Nolan Lawson / AndInTheClouds), integralmente reconstruído em Kotlin e Jetpack Compose.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Rodapé discreto
            Text(
                text = "Licença GNU GPLv3 • Zero Anúncios • 100% Offline",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HighlightItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
