package cat.itb.dam.m78.camerademo.camera.view

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner


//Composable específic per a la Previsualització de la Càmera
@Composable
fun VistaDeCamara(
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