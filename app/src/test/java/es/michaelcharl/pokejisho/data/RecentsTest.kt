package es.michaelcharl.pokejisho.data

import org.junit.Assert.assertEquals
import org.junit.Test

class RecentsTest {
    @Test
    fun newTermGoesToFront() {
        assertEquals(listOf("bbb", "aaa"), updatedRecents(listOf("aaa"), "bbb"))
    }

    @Test
    fun existingTermMovesToFrontWithoutDuplicating() {
        assertEquals(listOf("aaa", "bbb"), updatedRecents(listOf("bbb", "aaa"), "aaa"))
    }

    @Test
    fun termShorterThanTwoIsIgnored() {
        assertEquals(listOf("aaa"), updatedRecents(listOf("aaa"), "x"))
        assertEquals(listOf("aaa"), updatedRecents(listOf("aaa"), "  "))
    }

    @Test
    fun cappedAtFifteen() {
        val current = (1..15).map { "term$it" }
        val result = updatedRecents(current, "fresh")
        assertEquals(15, result.size)
        assertEquals("fresh", result.first())
        assertEquals(false, result.contains("term15"))
    }
}
