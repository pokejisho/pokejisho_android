package es.michaelcharl.pokejisho.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DictionaryEntryTest {
    @Test
    fun decodesCapitalizedAndLowercaseTypes() {
        val json = """
            [
              {"type":"Pokémon","english":"Bulbasaur","japanese":"フシギダネ","katakana":"フシギダネ","romaji":"fushigidane"},
              {"type":"move","english":"Tackle","japanese":"たいあたり","katakana":"タイアタリ","romaji":"taiatari"}
            ]
        """.trimIndent()
        val entries = parseDictionary(json)
        assertEquals(2, entries.size)
        assertEquals(EntryType.POKEMON, entries[0].type)
        assertEquals(EntryType.MOVE, entries[1].type)
    }

    @Test
    fun idIsTypeRawEnglishJapanese() {
        val e = DictionaryEntry(EntryType.ITEM, "Poké Ball", "モンスターボール", "モンスターボール", "monsutaabooru")
        assertEquals("item|Poké Ball|モンスターボール", e.id)
    }
}
