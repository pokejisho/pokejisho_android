package es.michaelcharl.pokejisho.search

import es.michaelcharl.pokejisho.data.DictionaryEntry
import es.michaelcharl.pokejisho.data.DictionaryStore
import es.michaelcharl.pokejisho.data.EntryType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchTest {
    private val store = DictionaryStore(
        listOf(
            DictionaryEntry(EntryType.POKEMON, "Pikachu", "ピカチュウ", "ピカチュウ", "pikachuu"),
            DictionaryEntry(EntryType.POKEMON, "Flabébé", "フラベベ", "フラベベ", "furabebe"),
            DictionaryEntry(EntryType.MOVE, "Quick Attack", "でんこうせっか", "デンコウセッカ", "denkousekka"),
            DictionaryEntry(EntryType.ITEM, "Poké Ball", "モンスターボール", "モンスターボール", "monsutaabooru"),
        )
    )

    @Test
    fun emptyQueryReturnsEmpty() {
        assertTrue(store.search("   ").isEmpty)
    }

    @Test
    fun hiraganaQueryMatchesKatakanaEntry() {
        val r = store.search("ぴかちゅう")
        assertEquals(listOf("Pikachu"), r.priority.map { it.english })
    }

    @Test
    fun exactEnglishMatchGoesToPriority() {
        val r = store.search("pikachu")
        assertEquals(listOf("Pikachu"), r.priority.map { it.english })
        assertTrue(r.results.isEmpty())
    }

    @Test
    fun containsEnglishMatchGoesToResults() {
        val r = store.search("pika")
        assertTrue(r.priority.isEmpty())
        assertEquals(listOf("Pikachu"), r.results.map { it.english })
    }

    @Test
    fun accentAndSpaceAndCaseAreNormalized() {
        assertEquals(listOf("Flabébé"), store.search("FLABEBE").priority.map { it.english })
        assertEquals(listOf("Poké Ball"), store.search("poke ball").priority.map { it.english })
    }

    @Test
    fun romajiMatches() {
        assertEquals(listOf("Quick Attack"), store.search("denkousekka").priority.map { it.english })
    }

    @Test
    fun typeFilterRestrictsResults() {
        val r = store.search("a", filter = EntryType.MOVE)
        assertTrue(r.priority.isEmpty() && r.results.all { it.type == EntryType.MOVE })
        assertTrue(r.results.isNotEmpty())
    }

    @Test
    fun katakanaShiftConvertsHiragana() {
        assertEquals("ピカチュウ", hiraganaToKatakana("ぴかちゅう"))
    }
}
