package br.com.cifrandroid.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebCaptureScreen(
    onBack: () -> Unit,
    onCapturedSong: (title: String, content: String) -> Unit
) {
    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var currentTitle by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        singleLine = true,
                        placeholder = { Text("URL ou busca...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val target = if (!urlInput.startsWith("http://") && !urlInput.startsWith("https://")) {
                            "https://www.google.com/search?q=" + urlInput.replace(" ", "+")
                        } else {
                            urlInput
                        }
                        webViewInstance?.loadUrl(target)
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Ir")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Abra a página ou selecione o texto",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            webViewInstance?.let { wv ->
                                // Extrai texto selecionado OU tag <pre> OU conteúdo do body
                                val script = """
                                    (function() {
                                        var sel = window.getSelection().toString();
                                        if (sel && sel.length > 5) return sel;
                                        var pre = document.querySelector('pre');
                                        if (pre) return pre.innerText;
                                        var code = document.querySelector('code');
                                        if (code) return code.innerText;
                                        return document.body.innerText;
                                    })();
                                """.trimIndent()

                                wv.evaluateJavascript(script) { result ->
                                    val cleaned = unescapeJsString(result)
                                    val songTitle = currentTitle
                                        .replace(" - Cifra Club", "")
                                        .replace(" | Cifra Club", "")
                                        .replace(" - Letras.mus.br", "")
                                        .trim()

                                    onCapturedSong(songTitle, cleaned)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Capturar Cifra", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                                url?.let { urlInput = it }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                currentTitle = view?.title ?: ""
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                currentTitle = title ?: ""
                            }
                        }

                        loadUrl("https://www.google.com")
                        webViewInstance = this
                    }
                },
                onRelease = { webView ->
                    // Destruição segura e livre de memory leak para Android 13/14+ / One UI
                    webView.stopLoading()
                    webView.webViewClient = WebViewClient()
                    webView.webChromeClient = WebChromeClient()
                    webView.loadUrl("about:blank")
                    webView.destroy()
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

/**
 * Remove escape do evaluateJavascript (ex: converte "\\n" para quebras reais de linha e remove aspas externas).
 */
private fun unescapeJsString(raw: String?): String {
    if (raw == null || raw == "null") return ""
    var s = raw
    if (s.startsWith("\"") && s.endsWith("\"") && s.length >= 2) {
        s = s.substring(1, s.length - 1)
    }
    return s
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
}
