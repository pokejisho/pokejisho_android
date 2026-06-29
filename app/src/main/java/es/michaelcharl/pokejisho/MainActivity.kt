package es.michaelcharl.pokejisho

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.michaelcharl.pokejisho.data.AppAppearance
import es.michaelcharl.pokejisho.ui.PokeJishoNavHost
import es.michaelcharl.pokejisho.ui.state.UserDataViewModel
import es.michaelcharl.pokejisho.ui.theme.PokéJishoTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as PokeJishoApp).container
        setContent {
            val userDataViewModel: UserDataViewModel =
                viewModel(factory = UserDataViewModel.factory(container.userData))
            val appearance by userDataViewModel.appearance.collectAsStateWithLifecycle()
            val darkTheme = when (appearance) {
                AppAppearance.SYSTEM -> isSystemInDarkTheme()
                AppAppearance.DARK -> true
                AppAppearance.LIGHT -> false
            }
            PokéJishoTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PokeJishoNavHost(container = container, userDataViewModel = userDataViewModel)
                }
            }
        }
    }
}
