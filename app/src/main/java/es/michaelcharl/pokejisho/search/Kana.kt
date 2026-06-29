package es.michaelcharl.pokejisho.search

/**
 * Hiragana → katakana via codepoint shift (U+3041–U+3096 → +0x60).
 * Matches the iOS `StringTransform.hiraganaToKatakana` for the kana range the dataset uses
 * (includes small kana and ゔ→ヴ). Pure Kotlin so search stays JVM-unit-testable.
 */
fun hiraganaToKatakana(input: String): String = buildString(input.length) {
    for (ch in input) {
        val code = ch.code
        if (code in 0x3041..0x3096) append((code + 0x60).toChar()) else append(ch)
    }
}
