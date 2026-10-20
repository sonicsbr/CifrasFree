package br.com.cifrandroid.music.transposer

import br.com.cifrandroid.music.model.Chord
import br.com.cifrandroid.music.model.NoteNaming
import br.com.cifrandroid.music.parser.ChordParser

/**
 * Motor de transposição tonal e capotraste.
 */
object Transposer {

    /**
     * Calcula o deslocamento real em semitons considerando capo e transposição manual.
     * Ex: Capo na casa 2 (+2) e transpor -2 semitons resulta em 0.
     */
    fun calculateEffectiveSemitones(capoFret: Int, manualSemitones: Int): Int {
        return manualSemitones - capoFret
    }

    /**
     * Transpõe um acorde isolado.
     */
    fun transposeChord(
        chord: Chord,
        semitones: Int,
        naming: NoteNaming = NoteNaming.ENGLISH_SHARPS
    ): Chord {
        return chord.transpose(semitones)
    }

    /**
     * Transpõe uma linha completa de texto que contenha acordes,
     * preservando ao máximo o alinhamento visual com a letra de baixo.
     */
    fun transposeLine(
        line: String,
        semitones: Int,
        naming: NoteNaming = NoteNaming.ENGLISH_SHARPS
    ): String {
        if (semitones == 0) return line

        val tokens = ChordParser.findChordsInLine(line)
        if (tokens.isEmpty()) return line

        val sb = StringBuilder()
        var lastIndex = 0

        for (token in tokens) {
            // Adiciona o texto entre o acorde anterior e o atual
            sb.append(line.substring(lastIndex, token.startIndex))

            // Transpõe o acorde
            val transposed = token.chord.transpose(semitones).format(naming)
            sb.append(transposed)

            lastIndex = token.endIndex
        }

        // Adiciona o restante da linha
        if (lastIndex < line.length) {
            sb.append(line.substring(lastIndex))
        }

        return sb.toString()
    }

    /**
     * Transpõe uma música inteira composta por múltiplas linhas de letra e cifra.
     */
    fun transposeSong(
        songText: String,
        semitones: Int,
        naming: NoteNaming = NoteNaming.ENGLISH_SHARPS
    ): String {
        if (semitones == 0) return songText

        return songText.lines().joinToString("\n") { line ->
            if (ChordParser.isChordLine(line)) {
                transposeLine(line, semitones, naming)
            } else {
                // Linha de letra comum, verifica se há algum acorde embutido [Chord] ou solto
                transposeLine(line, semitones, naming)
            }
        }
    }
}
