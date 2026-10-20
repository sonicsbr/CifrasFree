package br.com.cifrandroid.music.model

import kotlinx.serialization.Serializable

/**
 * Representa a digitação física de um acorde no braço do instrumento (trastes e cordas).
 *
 * Para violão (6 cordas): normalmente ordenado da corda mais grave para a mais aguda (E2, A2, D3, G3, B3, E4).
 * frets[i]:
 * - null: corda abafada / não tocada ('x')
 * - 0: corda solta ('0')
 * - 1..24: traste pressionado
 */
@Serializable
data class ChordVoicing(
    val frets: List<Int?>,
    val baseFret: Int = calculateBaseFret(frets),
    val barre: Int? = null,
    val fingers: List<Int?>? = null
) {
    val stringCount: Int get() = frets.size

    fun isMuted(stringIndex: Int): Boolean = frets.getOrNull(stringIndex) == null

    fun isOpen(stringIndex: Int): Boolean = frets.getOrNull(stringIndex) == 0

    fun fretAt(stringIndex: Int): Int? = frets.getOrNull(stringIndex)

    /**
     * Retorna a representação compacta padrão, por exemplo "x-3-2-0-1-0"
     */
    fun toDashString(): String {
        return frets.joinToString("-") { it?.toString() ?: "x" }
    }

    /**
     * Retorna sem traços, ex: "x32010"
     */
    fun toCompactString(): String {
        return frets.joinToString("") { it?.toString() ?: "x" }
    }

    companion object {
        fun calculateBaseFret(frets: List<Int?>): Int {
            val pressed = frets.filterNotNull().filter { it > 0 }
            if (pressed.isEmpty()) return 1
            val min = pressed.minOrNull() ?: 1
            val max = pressed.maxOrNull() ?: 1
            // Se o traste máximo for <= 4, exibe a partir da pestana (casa 1)
            return if (max <= 4) 1 else min
        }

        /**
         * Cria uma digitação a partir de uma lista de strings como ["x", "3", "2", "0", "1", "0"]
         */
        fun fromPositions(positions: List<String>): ChordVoicing {
            val frets = positions.map { pos ->
                when {
                    pos.equals("x", ignoreCase = true) -> null
                    else -> pos.toIntOrNull()
                }
            }
            return ChordVoicing(frets = frets)
        }
    }
}
