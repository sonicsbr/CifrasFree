package br.com.cifrandroid.data.importer

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import br.com.cifrandroid.data.local.dao.SongDao
import br.com.cifrandroid.data.local.entity.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

data class ImportProgress(
    val current: Int,
    val currentFileName: String,
    val isComplete: Boolean = false,
    val totalImported: Int = 0,
    val errorMessage: String? = null
)

/**
 * Utilitário seguro para importação de arquivos .txt e descompactação de .zip
 * via Storage Access Framework (ContentResolver), sem requerer permissões legadas de disco.
 */
class SongImporter(
    private val contentResolver: ContentResolver,
    private val songDao: SongDao
) {

    /**
     * Importa um único arquivo de texto (.txt) via Uri e salva no banco de dados.
     */
    suspend fun importSingleTextFile(uri: Uri): Result<SongEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val fileName = queryFileName(uri) ?: "Música Importada"
            val title = fileName.removeSuffix(".txt").removeSuffix(".TXT").trim()

            val content = contentResolver.openInputStream(uri)?.use { inputStream ->
                readStreamWithCharsetFallback(inputStream)
            } ?: throw IllegalStateException("Não foi possível abrir o arquivo selecionado.")

            val song = SongEntity(
                title = title,
                content = content,
                updatedAt = System.currentTimeMillis()
            )

            val id = songDao.insertSong(song)
            song.copy(id = id)
        }
    }

    /**
     * Autoextrai um arquivo .zip contendo arquivos .txt em background e salva em lote no banco.
     * Emite o progresso em tempo real para atualização da UI.
     */
    fun importZipFile(uri: Uri): Flow<ImportProgress> = flow {
        val songsToInsert = mutableListOf<SongEntity>()
        var count = 0

        try {
            contentResolver.openInputStream(uri)?.use { rawInputStream ->
                ZipInputStream(rawInputStream).use { zipStream ->
                    var entry: ZipEntry? = zipStream.nextEntry

                    while (entry != null) {
                        val name = entry.name
                        val isTextFile = name.endsWith(".txt", ignoreCase = true)
                        val isIgnored = entry.isDirectory || name.startsWith("__MACOSX") || name.startsWith(".")

                        if (isTextFile && !isIgnored) {
                            count++
                            val simpleName = name.substringAfterLast('/').substringAfterLast('\\')
                            val title = simpleName.removeSuffix(".txt").removeSuffix(".TXT").trim()

                            emit(ImportProgress(current = count, currentFileName = title))

                            val content = readCurrentZipEntry(zipStream)
                            if (content.isNotBlank()) {
                                songsToInsert.add(
                                    SongEntity(
                                        title = title,
                                        content = content,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                            }
                        }

                        zipStream.closeEntry()
                        entry = zipStream.nextEntry
                    }
                }
            } ?: throw IllegalStateException("Não foi possível abrir o arquivo ZIP.")

            // Inserção em lote no Room em uma única transação
            if (songsToInsert.isNotEmpty()) {
                songDao.insertSongs(songsToInsert)
            }

            emit(
                ImportProgress(
                    current = count,
                    currentFileName = "Concluído",
                    isComplete = true,
                    totalImported = songsToInsert.size
                )
            )

        } catch (e: Exception) {
            emit(
                ImportProgress(
                    current = count,
                    currentFileName = "",
                    isComplete = true,
                    totalImported = songsToInsert.size,
                    errorMessage = e.localizedMessage ?: "Erro na extração do ZIP"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun readCurrentZipEntry(zipStream: ZipInputStream): String {
        val byteArray = zipStream.readBytes()
        // Tenta decodificar em UTF-8; se falhar ou contiver caracteres de substituição estranhos, usa ISO-8859-1 (comum em cifras antigas do Windows)
        return try {
            val utf8 = String(byteArray, Charsets.UTF_8)
            if (utf8.contains("\uFFFD")) {
                String(byteArray, Charset.forName("ISO-8859-1"))
            } else {
                utf8
            }
        } catch (_: Exception) {
            String(byteArray, Charset.forName("ISO-8859-1"))
        }
    }

    private fun readStreamWithCharsetFallback(inputStream: InputStream): String {
        val bytes = inputStream.readBytes()
        return try {
            val utf8 = String(bytes, Charsets.UTF_8)
            if (utf8.contains("\uFFFD")) {
                String(bytes, Charset.forName("ISO-8859-1"))
            } else {
                utf8
            }
        } catch (_: Exception) {
            String(bytes, Charset.forName("ISO-8859-1"))
        }
    }

    private fun queryFileName(uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        name = cursor.getString(index)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path?.substringAfterLast('/')
        }
        return name
    }
}
