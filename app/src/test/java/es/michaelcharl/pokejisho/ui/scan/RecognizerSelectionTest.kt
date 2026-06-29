package es.michaelcharl.pokejisho.ui.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class RecognizerSelectionTest {
    @Test
    fun detectsJapaneseText() {
        assertEquals(TextRecognizerScriptHint.JAPANESE, scriptHintFor("ピカチュウ"))
    }

    @Test
    fun detectsLatinText() {
        assertEquals(TextRecognizerScriptHint.LATIN, scriptHintFor("Pikachu"))
    }

    @Test
    fun blankIsLatin() {
        assertEquals(TextRecognizerScriptHint.LATIN, scriptHintFor("   "))
    }
}
