package es.michaelcharl.pokejisho.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PokeApiParserTest {
    @Test
    fun parsesSpriteTypesDimensionsAndFlavor() {
        val json = """
            {
              "sprites": {"front_default": "https://img/1.png"},
              "types": [{"type":{"name":"grass"}},{"type":{"name":"poison"}}],
              "height": 7,
              "weight": 69,
              "flavor_text_entries": [
                {"flavor_text":"A strange\nseed.","language":{"name":"en"}},
                {"flavor_text":"へんなタネ。","language":{"name":"ja"}}
              ]
            }
        """.trimIndent()
        val d = PokeApiParser.parse(json)
        assertEquals("https://img/1.png", d.spriteUrl)
        assertEquals("Type", d.facts[0].label)
        assertEquals("grass, poison", d.facts[0].value)
        assertEquals("Height", d.facts[1].label)
        assertEquals("0.7 m", d.facts[1].value)
        assertEquals("6.9 kg", d.facts[2].value)
        assertEquals("A strange seed.", d.flavorText)
    }

    @Test
    fun parsesMoveStats() {
        val json = """{"power":40,"pp":35,"accuracy":100}"""
        val d = PokeApiParser.parse(json)
        assertEquals(listOf("Power", "PP", "Accuracy"), d.facts.map { it.label })
        assertEquals(listOf("40", "35", "100"), d.facts.map { it.value })
    }

    @Test
    fun missingFieldsYieldNullsAndEmptyFacts() {
        val d = PokeApiParser.parse("{}")
        assertNull(d.spriteUrl)
        assertNull(d.flavorText)
        assertEquals(emptyList<PokeApiDetail.Fact>(), d.facts)
    }
}
