package es.michaelcharl.pokejisho.ui

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import es.michaelcharl.pokejisho.BuildConfig
import es.michaelcharl.pokejisho.R
import es.michaelcharl.pokejisho.data.AppAppearance
import es.michaelcharl.pokejisho.ui.state.AppLanguage
import es.michaelcharl.pokejisho.ui.state.LocalizationManager
import es.michaelcharl.pokejisho.ui.state.UserDataViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(userDataViewModel: UserDataViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val appearance by userDataViewModel.appearance.collectAsStateWithLifecycle()
    var language by remember { mutableStateOf(LocalizationManager.current()) }
    val year = remember { Calendar.getInstance().get(Calendar.YEAR).toString() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.common_ok))
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            item { SectionTitle(stringResource(R.string.settings_display)) }

            item { Text(stringResource(R.string.settings_appearance), style = MaterialTheme.typography.bodyMedium) }
            items(AppAppearance.entries.size) { i ->
                val option = AppAppearance.entries[i]
                RadioRow(
                    label = stringResource(appearanceLabel(option)),
                    selected = appearance == option,
                    onSelect = { userDataViewModel.setAppearance(option) },
                )
            }

            item {
                Text(
                    stringResource(R.string.settings_language),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            items(AppLanguage.entries.size) { i ->
                val option = AppLanguage.entries[i]
                RadioRow(
                    label = languageLabel(option),
                    selected = language == option,
                    onSelect = {
                        language = option
                        LocalizationManager.set(option) // triggers a fast recreate
                    },
                )
            }

            item { SectionTitle(stringResource(R.string.about_title)) }
            item {
                Column {
                    Footnote(stringResource(R.string.about_fansite))
                    Footnote(stringResource(R.string.about_license))
                    Footnote(stringResource(R.string.about_copyright, year))
                    Footnote(stringResource(R.string.about_pokeapi))
                    TextButton(onClick = { openUrl(context, "https://pokeapi.co") }) {
                        Text(stringResource(R.string.about_pokeapi_link))
                    }
                    TextButton(onClick = { openUrl(context, "https://github.com/pokejisho/pokejisho_ios") }) {
                        Text(stringResource(R.string.about_github))
                    }
                    Footnote(
                        stringResource(R.string.settings_version) +
                            ": ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                    )
                }
            }
        }
    }
}

private fun appearanceLabel(a: AppAppearance): Int = when (a) {
    AppAppearance.SYSTEM -> R.string.settings_appearance_system
    AppAppearance.DARK -> R.string.settings_appearance_dark
    AppAppearance.LIGHT -> R.string.settings_appearance_light
}

@Composable
private fun languageLabel(language: AppLanguage): String = when (language) {
    AppLanguage.SYSTEM -> stringResource(R.string.settings_language_system)
    AppLanguage.EN -> "English"
    AppLanguage.JA -> "日本語"
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onSelect: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth().selectable(selected = selected, onClick = onSelect).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label, Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun Footnote(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 2.dp),
    )
}

private fun openUrl(context: android.content.Context, url: String) {
    CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
}
