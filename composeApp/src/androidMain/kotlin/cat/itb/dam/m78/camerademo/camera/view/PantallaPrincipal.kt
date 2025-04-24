package cat.itb.dam.m78.camerademo.camera.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner // Importació correcta per a compose



//Pantalla principal que es fa servir des de la MainActivity
@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    permisosDeCamera(
        context = context,
        granted = { granted ->
            hasCameraPermission = granted
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current


    //Disseny de la UI
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Espai per a la càmera (ocupa la major part de l'espai)
        Box(modifier = Modifier.weight(1f)) {
            if (hasCameraPermission) {
                VistaDeCamara(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    modifier = Modifier.fillMaxSize() // Fes que la previsualització ompli el Box
                )
            } else {
                // Mostra alguna cosa si no hi ha permís (o mentre es demana)
                Text("Es necessita permís de càmera", modifier = Modifier.align(Alignment.Center))
            }
        }

        // Botó sota la càmera
        Button (
            onClick = {
                // Acció del botó (per exemple, fer foto, analitzar imatge, etc.)
                // Aquí aniria la lògica per interactuar amb la càmera si fos necessari.
            },
            modifier = Modifier
                .padding(16.dp) // Afegeix una mica d'espai al voltant del botó
                .fillMaxWidth(0.8f) // Amplada del botó (80% de l'amplada)
        ) {
            Text("Acció càmera")
        }
    }
}
