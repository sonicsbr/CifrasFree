package br.com.cifrandroid.ui.web

object CapturedDataHolder {
    var capturedTitle: String = ""
    var capturedContent: String = ""

    fun clear() {
        capturedTitle = ""
        capturedContent = ""
    }
}
