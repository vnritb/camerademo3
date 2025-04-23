package cat.itb.dam.m78.camerademo.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cat.itb.dam.m78.camerademo.camera.view.PantallaPrincipal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //App()
            PantallaPrincipal()  //En camera preview
        }
    }
}