package cat.itb.dam.m78.camerademo.camera.view

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner // Importació correcta per a compose

//Pantalla principal que es fa servir des de la MainActivity
@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    //Objecte per gestionar el permís.  Llença la finestra de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Demanar permís quan el composable entra a la composició si encara no s'ha concedit
    LaunchedEffect(key1 = true) { // S'executa només una vegada a l'inici
        // Comprovar si el permís ja ha estat concedit
        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        //Pot ser que ja estigui concedit (si es va seleccionar l'opció de permetre sempre)
        if (permissionStatus == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        //En cas de no tenir permís (la primera vegada), es llença la finestra de permisos
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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
                // Aquí aniria la lògica per interactuar amb la càmera si fos necessari
            },
            modifier = Modifier
                .padding(16.dp) // Afegeix una mica d'espai al voltant del botó
                .fillMaxWidth(0.8f) // Amplada del botó (80% de l'amplada)
        ) {
            Text("Acció càmera")
        }
    }
}
