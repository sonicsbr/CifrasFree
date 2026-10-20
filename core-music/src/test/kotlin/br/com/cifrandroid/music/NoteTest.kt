package br.com.cifrandroid.music

import br.com.cifrandroid.music.model.Note
import br.com.cifrandroid.music.model.NoteNaming
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NoteTest {

    @Test
    fun `test transposing notes up and down`() {
        assertEquals(Note.C_SHARP, Note.C.transpose(1))
        assertEquals(Note.D, Note.C.transpose(2))
        assertEquals(Note.B, Note.C.transpose(-1))
        assertEquals(Note.A, Note.C.transpose(-3))
        assertEquals(Note.C, Note.C.transpose(12))
        assertEquals(Note.C, Note.C.transpose(-24))
    }

    @Test
    fun `test note parsing with different naming conventions and accidentals`() {
        assertEquals(Note.C, Note.parse("C"))
        assertEquals(Note.C, Note.parse("Dó"))
        assertEquals(Note.C, Note.parse("do"))

        assertEquals(Note.C_SHARP, Note.parse("C#"))
        assertEquals(Note.C_SHARP, Note.parse("Db"))
        assertEquals(Note.C_SHARP, Note.parse("C♯"))
        assertEquals(Note.C_SHARP, Note.parse("D♭"))
        assertEquals(Note.C_SHARP, Note.parse("Dó#"))
        assertEquals(Note.C_SHARP, Note.parse("Reb"))

        assertEquals(Note.B, Note.parse("B"))
        assertEquals(Note.B, Note.parse("Si"))
        assertEquals(Note.B, Note.parse("H")) // Germânico
    }

    @Test
    fun `test note formatting`() {
        assertEquals("C#", Note.C_SHARP.format(NoteNaming.ENGLISH_SHARPS))
        assertEquals("Db", Note.C_SHARP.format(NoteNaming.ENGLISH_FLATS))
        assertEquals("Dó#", Note.C_SHARP.format(NoteNaming.LATIN_SOLFEGE))
    }
}
