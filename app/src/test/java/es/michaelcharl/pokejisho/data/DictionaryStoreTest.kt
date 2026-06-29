package es.michaelcharl.pokejisho.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DictionaryStoreTest {
    private val sample = listOf(
        DictionaryEntry(EntryType.POKEMON, "Bulbasaur", "フシギダネ", "フシギダネ", "fushigidane"),
        DictionaryEntry(EntryType.MOVE, "Tackle", "たいあたり", "タイアタリ", "taiatari"),
    )

    @Test
    fun resolvesIdsAndSkipsUnknown() {
        val store = DictionaryStore(sample)
        val resolved = store.entries(listOf(sample[1].id, "does|not|exist"))
        assertEquals(listOf(sample[1]), resolved)
    }

    @Test
    fun duplicateIdsKeepFirst() {
        val dupe = sample[0].copy(romaji = "second")
        val store = DictionaryStore(listOf(sample[0], dupe))
        assertEquals("fushigidane", store.entries(listOf(sample[0].id)).single().romaji)
    }
}
