package br.com.cifrandroid.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")

    data object Stage : Screen("stage/{songId}?setlistId={setlistId}") {
        fun createRoute(songId: Long, setlistId: Long? = null): String {
            return if (setlistId != null) {
                "stage/$songId?setlistId=$setlistId"
            } else {
                "stage/$songId"
            }
        }
    }

    data object Editor : Screen("editor?songId={songId}&initialTitle={initialTitle}") {
        fun createRoute(songId: Long = 0L, initialTitle: String = ""): String {
            val encodedTitle = Uri.encode(initialTitle)
            return "editor?songId=$songId&initialTitle=$encodedTitle"
        }
    }

    data object WebCapture : Screen("web_capture")

    data object About : Screen("about")
}
