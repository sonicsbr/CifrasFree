package br.com.cifrandroid.music.model

/**
 * Qualidade harmônica básica de um acorde (tríade base).
 */
enum class ChordQuality(val symbol: String, val aliases: List<String>) {
    MAJOR("", listOf("", "M", "maj", "major")),
    MINOR("m", listOf("m", "min", "minor", "-")),
    DIMINISHED("dim", listOf("dim", "diminished", "°", "o")),
    AUGMENTED("aug", listOf("aug", "augmented", "+", "#5")),
    POWER("5", listOf("5")),
    SUSPENDED_2("sus2", listOf("sus2", "suspended2")),
    SUSPENDED_4("sus4", listOf("sus4", "sus", "suspended"));

    companion object {
        fun fromAlias(token: String): ChordQuality? {
            val lower = token.lowercase()
            return entries.firstOrNull { q ->
                q.symbol.equals(token, ignoreCase = true) ||
                    q.aliases.any { it.equals(token, ignoreCase = true) || it.equals(lower, ignoreCase = true) }
            }
        }
    }
}
