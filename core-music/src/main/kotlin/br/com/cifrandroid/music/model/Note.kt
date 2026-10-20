package br.com.cifrandroid.music.model

/**
 * Representa uma das 12 notas cromáticas da escala temperada ocidental.
 * O valor ordinal (0 a 11) representa o pitch class (0 = C).
 */
enum class Note(val pitchClass: Int) {
    C(0),
    C_SHARP(1),
    D(2),
    D_SHARP(3),
    E(4),
    F(5),
    F_SHARP(6),
    G(7),
    G_SHARP(8),
    A(9),
    A_SHARP(10),
    B(11);

    fun transpose(semitones: Int): Note {
        val total = (pitchClass + semitones) % 12
        val normalized = if (total < 0) total + 12 else total
        return fromPitchClass(normalized)
    }

    fun format(naming: NoteNaming = NoteNaming.ENGLISH_SHARPS): String {
        return naming.format(this)
    }

    companion object {
        fun fromPitchClass(pitchClass: Int): Note {
            val normalized = (pitchClass % 12).let { if (it < 0) it + 12 else it }
            return entries.first { it.pitchClass == normalized }
        }

        /**
         * Reconhece uma nota a partir de qualquer alias comum (#, b, latino, inglês).
         */
        fun parse(input: String): Note? {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) return null

            val normalized = trimmed
                .replace("♯", "#")
                .replace("♭", "b")
                .replace("—", "-")

            // Teste direto por convenções
            for (naming in NoteNaming.entries) {
                val found = naming.parse(normalized)
                if (found != null) return found
            }

            return null
        }
    }
}

/**
 * Convenções de nomenclatura de notas.
 */
enum class NoteNaming {
    ENGLISH_SHARPS {
        override fun format(note: Note): String = when (note) {
            Note.C -> "C"
            Note.C_SHARP -> "C#"
            Note.D -> "D"
            Note.D_SHARP -> "D#"
            Note.E -> "E"
            Note.F -> "F"
            Note.F_SHARP -> "F#"
            Note.G -> "G"
            Note.G_SHARP -> "G#"
            Note.A -> "A"
            Note.A_SHARP -> "A#"
            Note.B -> "B"
        }
    },
    ENGLISH_FLATS {
        override fun format(note: Note): String = when (note) {
            Note.C -> "C"
            Note.C_SHARP -> "Db"
            Note.D -> "D"
            Note.D_SHARP -> "Eb"
            Note.E -> "E"
            Note.F -> "F"
            Note.F_SHARP -> "Gb"
            Note.G -> "G"
            Note.G_SHARP -> "Ab"
            Note.A -> "A"
            Note.A_SHARP -> "Bb"
            Note.B -> "B"
        }
    },
    LATIN_SOLFEGE {
        override fun format(note: Note): String = when (note) {
            Note.C -> "Dó"
            Note.C_SHARP -> "Dó#"
            Note.D -> "Ré"
            Note.D_SHARP -> "Ré#"
            Note.E -> "Mi"
            Note.F -> "Fá"
            Note.F_SHARP -> "Fá#"
            Note.G -> "Sol"
            Note.G_SHARP -> "Sol#"
            Note.A -> "Lá"
            Note.A_SHARP -> "Lá#"
            Note.B -> "Si"
        }
    },
    GERMANIC {
        override fun format(note: Note): String = when (note) {
            Note.A_SHARP -> "B"
            Note.B -> "H"
            else -> ENGLISH_SHARPS.format(note)
        }
    };

    abstract fun format(note: Note): String

    fun parse(token: String): Note? {
        val lower = token.lowercase()
        return when (lower) {
            "c", "b#", "b♯", "do", "dó" -> Note.C
            "c#", "c♯", "db", "d♭", "do#", "dó#", "reb", "réb" -> Note.C_SHARP
            "d", "re", "ré" -> Note.D
            "d#", "d♯", "eb", "e♭", "re#", "ré#", "mib" -> Note.D_SHARP
            "e", "fb", "f♭", "mi" -> Note.E
            "f", "e#", "e♯", "fa", "fá" -> Note.F
            "f#", "f♯", "gb", "g♭", "fa#", "fá#", "solb" -> Note.F_SHARP
            "g", "sol", "so" -> Note.G
            "g#", "g♯", "ab", "a♭", "sol#", "lab" -> Note.G_SHARP
            "a", "la", "lá" -> Note.A
            "a#", "a♯", "bb", "b♭", "la#", "lá#", "sib", "tib" -> Note.A_SHARP
            "b", "cb", "c♭", "si", "ti", "h" -> Note.B
            else -> null
        }
    }
}
