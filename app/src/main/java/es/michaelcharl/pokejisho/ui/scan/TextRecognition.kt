package es.michaelcharl.pokejisho.ui.scan

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

/** Runs both Latin and Japanese recognizers and returns the de-duplicated text blocks. */
suspend fun recognizeBlocks(@Suppress("UNUSED_PARAMETER") context: Context, bitmap: Bitmap): List<String> {
    val image = InputImage.fromBitmap(bitmap, 0)
    val latin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val japanese = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

    try {
        val blocks = mutableListOf<String>()
        runCatching { latin.process(image).await() }.getOrNull()?.textBlocks?.forEach { blocks += it.text }
        runCatching { japanese.process(image).await() }.getOrNull()?.textBlocks?.forEach { blocks += it.text }

        return blocks
            .map { it.replace("\n", " ").trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    } finally {
        latin.close()
        japanese.close()
    }
}
