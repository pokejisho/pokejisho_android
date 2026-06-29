package es.michaelcharl.pokejisho.network

import es.michaelcharl.pokejisho.data.DictionaryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PokeApiService(private val client: OkHttpClient = OkHttpClient()) {
    private val cache = mutableMapOf<String, PokeApiDetail>()
    private val mutex = Mutex()

    /** Returns enrichment for [entry], or null for unenrichable types and on any failure. */
    suspend fun detail(entry: DictionaryEntry): PokeApiDetail? {
        val resource = pokeApiResource(entry.type) ?: return null
        val slug = pokeApiSlug(entry.english)
        val key = "${resource.path}/$slug"

        mutex.withLock { cache[key] }?.let { return it }

        val url = "https://pokeapi.co/api/v2/${resource.path}/$slug"
        val body = withContext(Dispatchers.IO) {
            try {
                client.newCall(Request.Builder().url(url).build()).execute().use { resp ->
                    if (resp.isSuccessful) resp.body?.string() else null
                }
            } catch (e: IOException) {
                null
            }
        } ?: return null

        val detail = try {
            PokeApiParser.parse(body)
        } catch (e: Exception) {
            return null
        }
        mutex.withLock { cache[key] = detail }
        return detail
    }
}
