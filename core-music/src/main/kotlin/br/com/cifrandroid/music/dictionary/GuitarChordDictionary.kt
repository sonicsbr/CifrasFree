package br.com.cifrandroid.music.dictionary

import br.com.cifrandroid.music.model.Chord
import br.com.cifrandroid.music.model.ChordVoicing
import kotlinx.serialization.json.*
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Dicionário otimizado de acordes para Violão/Guitarra (6 cordas).
 * Carrega a base uma única vez em memória para permitir buscas instantâneas O(1).
 */
class GuitarChordDictionary(
    private val chordMap: Map<String, List<ChordVoicing>>
) {
    /**
     * Retorna todas as digitações disponíveis para o acorde informado.
     * Testa automaticamente variações enarmônicas (C# vs Db).
     */
    fun getVoicings(chord: Chord): List<ChordVoicing> {
        for (key in chord.dictionaryLookupKeys()) {
            val voicings = chordMap[key]
            if (!voicings.isNullOrEmpty()) {
                return voicings
            }
        }
        return emptyList()
    }

    /**
     * Busca direta pelo nome exato (ex: "C", "Am", "G/B", "F#m7").
     */
    fun getVoicings(chordName: String): List<ChordVoicing> {
        return chordMap[chordName] ?: emptyList()
    }

    /**
     * Quantidade de acordes cadastrados na base.
     */
    val chordCount: Int get() = chordMap.size

    /**
     * Quantidade total de digitações/variações cadastradas.
     */
    val totalVoicingsCount: Int get() = chordMap.values.sumOf { it.size }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        /**
         * Carrega o dicionário a partir de um InputStream do arquivo JSON.
         */
        fun loadFromStream(inputStream: InputStream): GuitarChordDictionary {
            val map = mutableMapOf<String, List<ChordVoicing>>()

            val reader = InputStreamReader(inputStream, Charsets.UTF_8)
            val jsonElement = json.parseToJsonElement(reader.readText())
            val rootObject = jsonElement.jsonObject

            for ((chordName, variationsArray) in rootObject) {
                if (variationsArray is JsonArray) {
                    val voicingsList = mutableListOf<ChordVoicing>()

                    for (variation in variationsArray) {
                        val posArray = variation.jsonObject["positions"]?.jsonArray
                        if (posArray != null) {
                            val positions = posArray.map { it.jsonPrimitive.content }
                            voicingsList.add(ChordVoicing.fromPositions(positions))
                        }
                    }

                    if (voicingsList.isNotEmpty()) {
                        map[chordName] = voicingsList
                    }
                }
            }

            return GuitarChordDictionary(map)
        }

        /**
         * Carrega a partir dos recursos embutidos do classpath.
         */
        fun loadDefault(): GuitarChordDictionary {
            val resourceStream = GuitarChordDictionary::class.java.classLoader
                ?.getResourceAsStream("guitar_chords.json")
                ?: throw IllegalStateException("Recurso guitar_chords.json não encontrado no classpath.")

            return resourceStream.use { loadFromStream(it) }
        }
    }
}
