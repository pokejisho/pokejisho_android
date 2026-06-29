package es.michaelcharl.pokejisho.ui

import androidx.compose.runtime.Composable

@Composable
fun ScanScreen(onResult: (String) -> Unit, onBack: () -> Unit) {
    // Replaced with the real camera + ML Kit implementation in Task 15.
    onBack()
}
