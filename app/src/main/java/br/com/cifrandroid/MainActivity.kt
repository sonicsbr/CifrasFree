package br.com.cifrandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.cifrandroid.ui.about.AboutScreen
import br.com.cifrandroid.ui.editor.EditorScreen
import br.com.cifrandroid.ui.editor.EditorViewModel
import br.com.cifrandroid.ui.home.HomeScreen
import br.com.cifrandroid.ui.home.HomeViewModel
import br.com.cifrandroid.ui.navigation.Screen
import br.com.cifrandroid.ui.stage.StageScreen
import br.com.cifrandroid.ui.stage.StageViewModel
import br.com.cifrandroid.ui.theme.CifrasFreeTheme
import br.com.cifrandroid.ui.web.CapturedDataHolder
import br.com.cifrandroid.ui.web.WebCaptureScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as CifrasApplication).repository

        setContent {
            CifrasFreeTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    // Tela 1: Home (Biblioteca & Setlists)
                    composable(Screen.Home.route) {
                        val homeViewModel: HomeViewModel = viewModel(
                            factory = HomeViewModel.Factory(repository)
                        )
                        HomeScreen(
                            viewModel = homeViewModel,
                            onSongClick = { songId, setlistId ->
                                navController.navigate(Screen.Stage.createRoute(songId, setlistId))
                            },
                            onCreateManualSong = {
                                navController.navigate(Screen.Editor.createRoute())
                            },
                            onOpenWebBrowser = {
                                navController.navigate(Screen.WebCapture.route)
                            },
                            onOpenAbout = {
                                navController.navigate(Screen.About.route)
                            }
                        )
                    }

                    // Tela 2: Modo Palco
                    composable(
                        route = Screen.Stage.route,
                        arguments = listOf(
                            navArgument("songId") { type = NavType.LongType },
                            navArgument("setlistId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val songId = backStackEntry.arguments?.getLong("songId") ?: 0L
                        val setlistId = backStackEntry.arguments?.getString("setlistId")?.toLongOrNull()

                        val stageViewModel: StageViewModel = viewModel(
                            factory = StageViewModel.Factory(repository, songId, setlistId)
                        )

                        StageScreen(
                            viewModel = stageViewModel,
                            onBackToHome = {
                                navController.popBackStack(Screen.Home.route, inclusive = false)
                            },
                            onEditSong = { idToEdit ->
                                navController.navigate(Screen.Editor.createRoute(songId = idToEdit))
                            }
                        )
                    }

                    // Tela 3: Editor Manual
                    composable(
                        route = Screen.Editor.route,
                        arguments = listOf(
                            navArgument("songId") {
                                type = NavType.LongType
                                defaultValue = 0L
                            },
                            navArgument("initialTitle") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) { backStackEntry ->
                        val songId = backStackEntry.arguments?.getLong("songId") ?: 0L
                        val initialTitle = backStackEntry.arguments?.getString("initialTitle") ?: ""

                        val editorViewModel: EditorViewModel = viewModel(
                            factory = EditorViewModel.Factory(repository, songId, initialTitle)
                        )

                        EditorScreen(
                            viewModel = editorViewModel,
                            onBack = { navController.popBackStack() },
                            onSaveSuccess = { savedSongId ->
                                navController.popBackStack()
                                navController.navigate(Screen.Stage.createRoute(savedSongId))
                            }
                        )
                    }

                    // Tela 4: Navegador Web (Captura de Cifras)
                    composable(Screen.WebCapture.route) {
                        WebCaptureScreen(
                            onBack = { navController.popBackStack() },
                            onCapturedSong = { capturedTitle, capturedContent ->
                                CapturedDataHolder.capturedTitle = capturedTitle
                                CapturedDataHolder.capturedContent = capturedContent
                                navController.navigate(Screen.Editor.createRoute(initialTitle = capturedTitle))
                            }
                        )
                    }

                    // Tela 5: Sobre o App
                    composable(Screen.About.route) {
                        AboutScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
