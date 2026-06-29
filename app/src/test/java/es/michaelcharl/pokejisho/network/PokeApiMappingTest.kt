package es.michaelcharl.pokejisho.network

import es.michaelcharl.pokejisho.data.EntryType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PokeApiMappingTest {
    @Test
    fun mapsEnrichableTypes() {
        assertEquals(PokeApiResource.POKEMON, pokeApiResource(EntryType.POKEMON))
        assertEquals(PokeApiResource.MOVE, pokeApiResource(EntryType.MOVE))
        assertEquals(PokeApiResource.ITEM, pokeApiResource(EntryType.ITEM))
        assertEquals(PokeApiResource.ABILITY, pokeApiResource(EntryType.ABILITY))
        assertEquals(PokeApiResource.NATURE, pokeApiResource(EntryType.NATURE))
    }

    @Test
    fun characterAndLocationHaveNoResource() {
        assertNull(pokeApiResource(EntryType.CHARACTER))
        assertNull(pokeApiResource(EntryType.LOCATION))
    }

    @Test
    fun slugLowercasesStripsPunctuationAndHyphenatesSpaces() {
        assertEquals("mr-mime", pokeApiSlug("Mr. Mime"))
        assertEquals("farfetchd", pokeApiSlug("Farfetch'd"))
        assertEquals("type-null", pokeApiSlug("Type: Null"))
        assertEquals("poke-ball", pokeApiSlug("Poke Ball"))
    }
}
