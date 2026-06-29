package es.michaelcharl.pokejisho.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import es.michaelcharl.pokejisho.R
import es.michaelcharl.pokejisho.data.DictionaryEntry
import es.michaelcharl.pokejisho.network.PokeApiDetail
import es.michaelcharl.pokejisho.network.PokeApiService
import es.michaelcharl.pokejisho.ui.state.UserDataViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    entry: DictionaryEntry,
    pokeApiService: PokeApiService,
    userDataViewModel: UserDataViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val favorites by userDataViewModel.favorites.collectAsStateWithLifecycle()
    val isFavorite = entry.id in favorites

    var detail by remember(entry.id) { mutableStateOf<PokeApiDetail?>(null) }
    var loading by remember(entry.id) { mutableStateOf(false) }

    LaunchedEffect(entry.id) {
        userDataViewModel.addRecent(entry.english)
        loading = true
        detail = pokeApiService.detail(entry)
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entry.english) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.common_back))
                    }
                },
                actions = {
                    IconButton(onClick = { copyEntry(context, entry) }) {
                        Icon(Icons.Filled.ContentCopy, stringResource(R.string.detail_copy))
                    }
                    IconButton(onClick = { userDataViewModel.toggleFavorite(entry.id) }) {
                        if (isFavorite) {
                            Icon(Icons.Filled.Star, stringResource(R.string.favorite_remove))
                        } else {
                            Icon(Icons.Outlined.StarBorder, stringResource(R.string.favorite_add))
                        }
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                if (loading) {
                    Box(Modifier.fillMaxWidth().padding(8.dp), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                detail?.spriteUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(140.dp).padding(8.dp),
                    )
                }
                FieldRow("English", entry.english)
                FieldRow("日本語", entry.japanese)
                FieldRow(stringResource(R.string.detail_katakana), entry.katakana)
                FieldRow(stringResource(R.string.detail_romaji), entry.romaji)
            }

            detail?.facts?.takeIf { it.isNotEmpty() }?.let { facts ->
                item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
                items(facts.size) { i -> FieldRow(facts[i].label, facts[i].value) }
            }

            detail?.flavorText?.takeIf { it.isNotEmpty() }?.let { flavor ->
                item {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text(flavor, Modifier.padding(vertical = 8.dp))
                }
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text(
                    stringResource(R.string.detail_learn_more),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                TextButton(onClick = { openUrl(context, bulbapediaUrl(entry)) }) {
                    Text(stringResource(R.string.detail_bulbapedia))
                }
                TextButton(onClick = { openUrl(context, pokewikiUrl(entry)) }) {
                    Text(stringResource(R.string.detail_pokewiki))
                }
            }
        }
    }
}

@Composable
private fun FieldRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.padding(start = 16.dp))
    }
}

private fun copyEntry(context: Context, entry: DictionaryEntry) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("PokéJisho", "${entry.english} / ${entry.japanese}"))
}

private fun openUrl(context: Context, url: String) {
    CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
}

private fun bulbapediaUrl(entry: DictionaryEntry): String =
    "https://bulbapedia.bulbagarden.net/wiki/" + entry.english.replace(" ", "_")

private fun pokewikiUrl(entry: DictionaryEntry): String =
    "https://wiki.xn--rckteqa2e.com/wiki/" + URLEncoder.encode(entry.japanese, "UTF-8").replace("+", "%20")
