package br.com.cifrandroid.music.model

/**
 * Representa um acorde completo com fundamental, qualidade, extensões e baixo opcional.
 */
data class Chord(
    val root: Note,
    val quality: ChordQuality = ChordQuality.MAJOR,
    val extension: String? = null,
    val added: String? = null,
    val suspended: String? = null,
    val bass: Note? = null
) {
    /**
     * Transpõe o acorde por um número de semitons (positivo para subir tom, negativo para descer).
     * O baixo (se presente) também é transposto de acordo.
     */
    fun transpose(semitones: Int): Chord {
        if (semitones == 0) return this
        return copy(
            root = root.transpose(semitones),
            bass = bass?.transpose(semitones)
        )
    }

    /**
     * Formata o nome do acorde de acordo com a convenção de notas escolhida.
     */
    fun format(naming: NoteNaming = NoteNaming.ENGLISH_SHARPS): String {
        val sb = StringBuilder()
        sb.append(root.format(naming))

        if (extension != null) {
            sb.append(extension)
        } else {
            sb.append(quality.symbol)
        }

        if (added != null) {
            sb.append(added)
        }

        if (suspended != null) {
            sb.append(suspended)
        }

        if (bass != null) {
            sb.append("/").append(bass.format(naming))
        }

        return sb.toString()
    }

    /**
     * Gera chaves equivalentes para busca no dicionário JSON
     * (testando tanto grafia com sustenido quanto com bemol e variações de alias).
     */
    fun dictionaryLookupKeys(): List<String> {
        val keys = mutableListOf<String>()

        val rootSharp = root.format(NoteNaming.ENGLISH_SHARPS)
        val rootFlat = root.format(NoteNaming.ENGLISH_FLATS)

        val suffix = buildString {
            if (extension != null) {
                append(extension)
            } else {
                append(quality.symbol)
            }
            if (added != null) append(added)
            if (suspended != null) append(suspended)
        }

        val bassSharp = bass?.format(NoteNaming.ENGLISH_SHARPS)?.let { "/$it" } ?: ""
        val bassFlat = bass?.format(NoteNaming.ENGLISH_FLATS)?.let { "/$it" } ?: ""

        keys.add("$rootSharp$suffix$bassSharp")
        if (rootSharp != rootFlat || bassSharp != bassFlat) {
            keys.add("$rootFlat$suffix$bassFlat")
            keys.add("$rootSharp$suffix$bassFlat")
            keys.add("$rootFlat$suffix$bassSharp")
        }

        return keys.distinct()
    }

    override fun toString(): String = format()
}
