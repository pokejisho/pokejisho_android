package es.michaelcharl.pokejisho.ui.scan

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

enum class TextRecognizerScriptHint { LATIN, JAPANESE }

/** True if the string contains any Hiragana/Katakana/CJK ideograph. */
fun scriptHintFor(text: String): TextRecognizerScriptHint {
    val hasCjk = text.any { ch ->
        val code = ch.code
        code in 0x3040..0x30FF || code in 0x4E00..0x9FFF
    }
    return if (hasCjk) TextRecognizerScriptHint.JAPANESE else TextRecognizerScriptHint.LATIN
}

/** Runs both Latin and Japanese recognizers and returns the de-duplicated text blocks. */
suspend fun recognizeBlocks(@Suppress("UNUSED_PARAMETER") context: Context, bitmap: Bitmap): List<String> {
    val image = InputImage.fromBitmap(bitmap, 0)
    val latin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val japanese = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

    val blocks = mutableListOf<String>()
    runCatching { latin.process(image).await() }.getOrNull()?.textBlocks?.forEach { blocks += it.text }
    runCatching { japanese.process(image).await() }.getOrNull()?.textBlocks?.forEach { blocks += it.text }

    return blocks
        .map { it.replace("\n", " ").trim() }
        .filter { it.isNotEmpty() }
        .distinct()
}
