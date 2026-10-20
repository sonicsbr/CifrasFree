package br.com.cifrandroid.music

import br.com.cifrandroid.music.dictionary.GuitarChordDictionary
import br.com.cifrandroid.music.model.Chord
import br.com.cifrandroid.music.model.ChordQuality
import br.com.cifrandroid.music.model.Note
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class GuitarChordDictionaryTest {

    companion object {
        private lateinit var dictionary: GuitarChordDictionary

        @BeforeAll
        @JvmStatic
        fun setup() {
            val startTime = System.currentTimeMillis()
            dictionary = GuitarChordDictionary.loadDefault()
            val elapsed = System.currentTimeMillis() - startTime
            println("Dicionário carregado em ${elapsed}ms. Total de acordes: ${dictionary.chordCount}, total de digitações: ${dictionary.totalVoicingsCount}")
        }
    }

    @Test
    fun `test loading dictionary successfully`() {
        assertTrue(dictionary.chordCount > 100, "Deveria haver centenas de acordes no dicionário")
    }

    @Test
    fun `test finding C major chord voicings`() {
        val cChord = Chord(root = Note.C, quality = ChordQuality.MAJOR)
        val voicings = dictionary.getVoicings(cChord)

        assertFalse(voicings.isEmpty(), "Deveria encontrar digitações para C")
        
        // Verifica se a primeira digitação é a clássica x-3-2-0-1-0
        val classicC = voicings.first()
        assertEquals("x-3-2-0-1-0", classicC.toDashString())
    }

    @Test
    fun `test finding minor and slash chords`() {
        val amVoicings = dictionary.getVoicings("Am")
        assertFalse(amVoicings.isEmpty(), "Deveria encontrar digitações para Am")

        val slashVoicings = dictionary.getVoicings("C/G")
        assertFalse(slashVoicings.isEmpty(), "Deveria encontrar digitações para C/G")
    }
}
