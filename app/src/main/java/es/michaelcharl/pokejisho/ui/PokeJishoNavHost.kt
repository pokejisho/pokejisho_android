package es.michaelcharl.pokejisho.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import es.michaelcharl.pokejisho.AppContainer
import es.michaelcharl.pokejisho.ui.state.SearchViewModel
import es.michaelcharl.pokejisho.ui.state.UserDataViewModel

private const val SCAN_RESULT_KEY = "scan_query"

@Composable
fun PokeJishoNavHost(container: AppContainer, userDataViewModel: UserDataViewModel) {
    val navController = rememberNavController()
    val searchViewModel: SearchViewModel =
        viewModel(factory = SearchViewModel.factory(container.dictionaryStore))

    NavHost(navController = navController, startDestination = "search") {
        composable("search") { backStackEntry ->
            // Receive a query handed back from the scan screen.
            val handle = backStackEntry.savedStateHandle
            LaunchedEffect(Unit) {
                handle.getStateFlow<String?>(SCAN_RESULT_KEY, null).collect { result ->
                    if (result != null) {
                        searchViewModel.setQuery(result)
                        handle[SCAN_RESULT_KEY] = null
                    }
                }
            }
            SearchScreen(
                searchViewModel = searchViewModel,
                userDataViewModel = userDataViewModel,
                store = container.dictionaryStore,
                onOpenEntry = { entry ->
                    navController.navigate("detail/${Uri.encode(entry.id)}")
                },
                onOpenSettings = { navController.navigate("settings") },
                onScan = { navController.navigate("scan") },
            )
        }

        composable(
            route = "detail/{entryId}",
            arguments = listOf(navArgument("entryId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val entryId = Uri.decode(backStackEntry.arguments?.getString("entryId").orEmpty())
            val entry = container.dictionaryStore.entries(listOf(entryId)).firstOrNull()
            if (entry == null) {
                navController.popBackStack()
            } else {
                DetailScreen(
                    entry = entry,
                    pokeApiService = container.pokeApiService,
                    userDataViewModel = userDataViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable("settings") {
            SettingsScreen(
                userDataViewModel = userDataViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable("scan") {
            ScanScreen(
                onResult = { query ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(SCAN_RESULT_KEY, query)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
