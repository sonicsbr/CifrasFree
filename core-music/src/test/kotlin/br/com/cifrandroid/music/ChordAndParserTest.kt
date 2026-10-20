package br.com.cifrandroid.music

import br.com.cifrandroid.music.model.Chord
import br.com.cifrandroid.music.model.ChordQuality
import br.com.cifrandroid.music.model.Note
import br.com.cifrandroid.music.model.NoteNaming
import br.com.cifrandroid.music.parser.ChordParser
import br.com.cifrandroid.music.transposer.Transposer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ChordAndParserTest {

    @Test
    fun `test chord transposition with bass note`() {
        // Am/G transposto +2 semitons -> Bm/A
        val chord = Chord(
            root = Note.A,
            quality = ChordQuality.MINOR,
            bass = Note.G
        )

        val transposed = chord.transpose(2)
        assertEquals(Note.B, transposed.root)
        assertEquals(Note.A, transposed.bass)
        assertEquals(ChordQuality.MINOR, transposed.quality)
        assertEquals("Bm/A", transposed.format())
    }

    @Test
    fun `test parsing single chords`() {
        val c = ChordParser.parseChord("C")
        assertNotNull(c)
        assertEquals(Note.C, c!!.root)
        assertEquals(ChordQuality.MAJOR, c.quality)

        val fSharpMinor = ChordParser.parseChord("F#m")
        assertNotNull(fSharpMinor)
        assertEquals(Note.F_SHARP, fSharpMinor!!.root)
        assertEquals(ChordQuality.MINOR, fSharpMinor.quality)

        val slashChord = ChordParser.parseChord("C/G")
        assertNotNull(slashChord)
        assertEquals(Note.C, slashChord!!.root)
        assertEquals(Note.G, slashChord.bass)

        val complexChord = ChordParser.parseChord("F#m7/C#")
        assertNotNull(complexChord)
        assertEquals(Note.F_SHARP, complexChord!!.root)
        assertEquals(Note.C_SHARP, complexChord.bass)
        assertEquals("m7", complexChord.extension)

        val latinChord = ChordParser.parseChord("Dó#m7")
        assertNotNull(latinChord)
        assertEquals(Note.C_SHARP, latinChord!!.root)
        assertEquals(ChordQuality.MINOR, latinChord.quality)
    }

    @Test
    fun `test find chords in lyrics line`() {
        val line = "  C         G/B        Am          F  "
        val tokens = ChordParser.findChordsInLine(line)

        assertEquals(4, tokens.size)
        assertEquals("C", tokens[0].rawText)
        assertEquals("G/B", tokens[1].rawText)
        assertEquals("Am", tokens[2].rawText)
        assertEquals("F", tokens[3].rawText)

        assertTrue(ChordParser.isChordLine(line))
    }

    @Test
    fun `test transposing a line of chords and lyrics`() {
        val line = "C       G       Am      F"
        val transposed = Transposer.transposeLine(line, 2) // +2 semitons: C->D, G->A, Am->Bm, F->G

        assertEquals("D       A       Bm      G", transposed)
    }
}
