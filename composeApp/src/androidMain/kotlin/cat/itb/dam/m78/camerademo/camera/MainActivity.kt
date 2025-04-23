package cat.itb.dam.m78.camerademo.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cat.itb.dam.m78.camerademo.camera.view.CameraPreview
import cat.itb.dam.m78.camerademo.camera.view.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //App()
            MainScreen()  //En camera preview
        }
    }
}