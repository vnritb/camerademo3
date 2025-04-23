package cat.itb.dam.m78.camerademo.camera.view

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner // Importació correcta per a compose

//Pantalla principal que es fa servir des de la MainActivity
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    //Gestió de Permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            // Opcional: Mostrar missatge o gestionar el cas de permís denegat
            //if (!granted) {
            //    // No s'ha donat permís
            //}
        }
    )

    // Demanar permís quan el composable entra a la composició si encara no s'ha concedit
    LaunchedEffect(key1 = true) { // S'executa només una vegada a l'inici
        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionStatus == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
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
                CameraPreview(
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
                // Log.d("MainScreen", "Botó presionado!") // Log comentat
                // Aquí aniria la lògica per interactuar amb la càmera si fos necessari
            },
            modifier = Modifier
                .padding(16.dp) // Afegeix una mica d'espai al voltant del botó
                .fillMaxWidth(0.8f) // Amplada del botó (80% de l'amplada)
        ) {
            Text("Acció Càmera")
        }
    }
}


// --- Composable específic per a la Previsualització de la Càmera ---
@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var previewView: PreviewView? = null // Mantenim la referència per actualitzar

    AndroidView(
        factory = { ctx ->
            val view = PreviewView(ctx).apply {
                // Pots configurar scaleType, implementationMode aquí si és necessari
                // implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                // scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            previewView = view // Guardem la referència
            setupCamera(context, lifecycleOwner, view, cameraProviderFuture)
            view // Retorna la vista creada
        },
        modifier = modifier,
        update = { view ->
            // Es crida si el modifier o altres paràmetres canvien.
            // Podríem reiniciar la càmera aquí si fos necessari, però
            // generalment setupCamera se n'encarrega amb el lifecycleOwner.
            // Si canvies la càmera (frontal/posterior), necessitaries cridar setupCamera de nou.
            // Log.d("CameraPreview", "AndroidView updated") // Log comentat
            // Assegura't que la previewView referenciada sigui l'actual
            if (previewView != view) {
                previewView = view
                // Podries necessitar reconfigurar si la view canvia dràsticament
                // setupCamera(context, lifecycleOwner, view, cameraProviderFuture)
            }
        }
    )

    // Efecte per netejar quan el composable desapareix
    DisposableEffect(key1 = lifecycleOwner) {
        onDispose {
            // Desvincular la càmara del cicle de vida
            // Intenta desvincular tots els casos d'ús en sortir
            // Això és important per alliberar la càmera correctament
            try {
                cameraProviderFuture.get()?.unbindAll()
            } catch (e: Exception) {
                // Error al desvincular cámara
            }
        }
    }
}

// Funció auxiliar per configurar CameraX
private fun setupCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraProviderFuture: com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider>
) {
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            // Configuració del cas d'ús Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Selecció de la càmera (posterior per defecte)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Desvincula abans de tornar a vincular (important per evitar errors)
            cameraProvider.unbindAll()

            // Vincula els casos d'ús al cicle de vida
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview // Es poden afegir altres casos d'ús aquí (ImageCapture, ImageAnalysis)
            )
            // Càmara vinculada correctament

        } catch (exc: Exception) {
            // Error al vincular casos d'us de la càmara
        }
    }, ContextCompat.getMainExecutor(context)) // Executa en el fil principal
}