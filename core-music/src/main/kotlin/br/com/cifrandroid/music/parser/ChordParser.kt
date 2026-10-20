package br.com.cifrandroid.music.parser

import br.com.cifrandroid.music.model.Chord
import br.com.cifrandroid.music.model.ChordQuality
import br.com.cifrandroid.music.model.Note

data class ChordToken(
    val chord: Chord,
    val startIndex: Int,
    val endIndex: Int,
    val rawText: String
)

/**
 * Parser de acordes robusto e desacoplado de plataforma.
 * Suporta acordes cifrados comuns no Brasil e internacionalmente.
 */
object ChordParser {

    // Regex para a fundamental (C, C#, Db, Lá, Sol#, etc.)
    private const val ROOT_REGEX = "(?:[A-G][#b♯♭]?|Dó[#b♯♭]?|Do[#b♯♭]?|Ré[#b♯♭]?|Re[#b♯♭]?|Mi[#b♯♭]?|Fá[#b♯♭]?|Fa[#b♯♭]?|Sol[#b♯♭]?|Lá[#b♯♭]?|La[#b♯♭]?|Si[#b♯♭]?)"

    // Regex de extensões, qualidade e adições comuns
    private const val EXTENSION_REGEX = """(?:maj7|Maj7|7M|M7|m7b5|m7\(b5\)|ø|dim7|dim|°|aug|\+|m7|min7|-7|m9|min9|maj9|M9|m11|m13|7sus4|sus4|sus2|sus|add9|add11|6\/9|69|6|m6|7b9|7#9|7b5|7#5|7\(9\)|7\/9|7|9|11|13|m|min|-|5)?"""

    // Baixo invertido opcional (/G, /F#, etc.)
    private const val BASS_REGEX = """(?:\/($ROOT_REGEX))?"""

    // Regex completa para acorde isolado
    private val STANDALONE_CHORD_PATTERN = Regex(
        "^($ROOT_REGEX)($EXTENSION_REGEX)$BASS_REGEX$",
        RegexOption.IGNORE_CASE
    )

    // Regex para encontrar acordes dentro de uma linha de texto/cifra
    private val IN_TEXT_CHORD_PATTERN = Regex(
        """\b($ROOT_REGEX)($EXTENSION_REGEX)(?:\/($ROOT_REGEX))?\b"""
    )

    /**
     * Tenta fazer o parse de uma string representando um acorde isolado.
     * Retorna null se não for um acorde válido.
     */
    fun parseChord(raw: String): Chord? {
        val trimmed = raw.trim()
        val match = STANDALONE_CHORD_PATTERN.matchEntire(trimmed) ?: return null

        val rootStr = match.groupValues[1]
        val extStr = match.groupValues[2]
        val bassStr = match.groupValues[3].takeIf { it.isNotEmpty() }

        val root = Note.parse(rootStr) ?: return null
        val bass = bassStr?.let { Note.parse(it) }

        val (quality, extension, added, sus) = analyzeQualityAndExtensions(extStr)

        return Chord(
            root = root,
            quality = quality,
            extension = extension,
            added = added,
            suspended = sus,
            bass = bass
        )
    }

    /**
     * Encontra todas as ocorrências de acordes em uma linha de texto com seus índices de início e fim.
     */
    fun findChordsInLine(line: String): List<ChordToken> {
        val tokens = mutableListOf<ChordToken>()

        for (match in IN_TEXT_CHORD_PATTERN.findAll(line)) {
            val rawChord = match.value
            val chord = parseChord(rawChord)
            if (chord != null) {
                // Heurística de proteção: evitar falsos positivos com palavras de 1 letra como "A" ou "E"
                // se estiverem no meio de uma frase em português
                if ((rawChord == "A" || rawChord == "E") && isSurroundedByWords(line, match.range.first, match.range.last)) {
                    continue
                }

                tokens.add(
                    ChordToken(
                        chord = chord,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        rawText = rawChord
                    )
                )
            }
        }

        return tokens
    }

    /**
     * Verifica se uma linha é predominantemente composta por acordes (linha de cifra)
     */
    fun isChordLine(line: String): Boolean {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return false

        val chords = findChordsInLine(line)
        if (chords.isEmpty()) return false

        // Calcula quantos caracteres não-espaço pertencem a acordes
        val chordCharsCount = chords.sumOf { it.rawText.length }
        val nonWhitespaceChars = trimmed.count { !it.isWhitespace() && it != '|' && it != '-' }

        if (nonWhitespaceChars == 0) return false
        return (chordCharsCount.toDouble() / nonWhitespaceChars.toDouble()) >= 0.6
    }

    private fun isSurroundedByWords(line: String, start: Int, end: Int): Boolean {
        val before = if (start > 0) line[start - 1] else ' '
        val after = if (end < line.length - 1) line[end + 1] else ' '
        // Se antes e depois forem letras normais, não é acorde isolado
        return before.isLetter() || after.isLetter()
    }

    private fun analyzeQualityAndExtensions(ext: String): ExtParts {
        if (ext.isEmpty()) {
            return ExtParts(ChordQuality.MAJOR, null, null, null)
        }

        val lower = ext.lowercase()

        return when {
            lower == "m" || lower == "min" || lower == "-" ->
                ExtParts(ChordQuality.MINOR, null, null, null)

            lower.startsWith("m7b5") || lower.contains("ø") ->
                ExtParts(ChordQuality.MINOR, "m7b5", null, null)

            lower.startsWith("dim") || lower == "°" || lower == "o" ->
                ExtParts(ChordQuality.DIMINISHED, ext, null, null)

            lower.startsWith("aug") || lower == "+" ->
                ExtParts(ChordQuality.AUGMENTED, ext, null, null)

            lower == "5" ->
                ExtParts(ChordQuality.POWER, null, null, null)

            lower.contains("sus4") ->
                ExtParts(ChordQuality.SUSPENDED_4, null, null, ext)

            lower.contains("sus2") ->
                ExtParts(ChordQuality.SUSPENDED_2, null, null, ext)

            lower.startsWith("m") ->
                ExtParts(ChordQuality.MINOR, ext, null, null)

            lower.startsWith("add") ->
                ExtParts(ChordQuality.MAJOR, null, ext, null)

            else ->
                ExtParts(ChordQuality.MAJOR, ext, null, null)
        }
    }

    private data class ExtParts(
        val quality: ChordQuality,
        val extension: String?,
        val added: String?,
        val sus: String?
    )
}
