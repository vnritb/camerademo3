package cat.itb.dam.m78.camerademo.camera

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CameraDemo3",
    ) {
        App()
    }
}