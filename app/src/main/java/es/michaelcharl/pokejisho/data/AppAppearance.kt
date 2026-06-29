package es.michaelcharl.pokejisho.data

enum class AppAppearance(val raw: String) {
    SYSTEM("system"),
    DARK("dark"),
    LIGHT("light"),
}

fun appAppearanceFrom(raw: String?): AppAppearance =
    AppAppearance.entries.firstOrNull { it.raw == raw } ?: AppAppearance.SYSTEM
