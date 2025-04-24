package cat.itb.dam.m78.camerademo.camera.view

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
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
    //Punt d'accés únic a la càmera
    //Vincular/desvincular casos d'us de la càmara al cicle de vida de la Activity
    //Seleccionar càmeres i obtenir-ne informació,
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    // Aquest és pròpiament el visor de la càmara (no és un composable)
    var previewView: PreviewView? = null

    // AndroidView és un composable que permet integrar vistes d'Android en Jetpack Compose
    // Paràmetres:
    // - factory: funció que crea la vista d'Android (en aquest cas, PreviewView)
    // - modifier: modificador per aplicar a la vista (en principi va buit).
    // - update: funció que s'executa quan la vista es torna a dibuixar
    AndroidView(
        factory = { ctx -> val view = PreviewView(ctx).apply {
                // Configurar aquí: scaleType i implementationMode
                // implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                // scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            previewView = view // Guardem la referència
            setupCamera(context, lifecycleOwner, view, cameraProviderFuture)
            view // Retorna la vista creada
        },
        modifier = modifier, //S'agafa el modificador passat com a paràmetre
        update = { view ->
            // Es crida si el modifier o altres paràmetres canvien.
            // Podríem reiniciar la càmera aquí si fos necessari, però
            // generalment setupCamera se n'encarrega amb el lifecycleOwner.
            // Si es canvia la  càmera (frontal/posterior), s'ha de  cridar a setupCamera de nou.
            // Ens hem d'assegurar que la previewView referenciada sigui l'actual
            if (previewView != view) {
                previewView = view  //En cas d'un canvi dràstic de la v
            }
        }
    )

    // Efecte per netejar quan el composable desapareix
    DisposableEffect(key1 = lifecycleOwner) {
        onDispose {
            // Desvincular la càmera del cicle de vida
            // Intentar desvincular tots els casos d'ús en sortir
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
    /*camaraProviderFuture és un objecte que conté la instància de ProcessCameraProvider
     * i s'assegura que la càmera estigui disponible abans de vincular-la
     * El processCameraProvider és l'objecte que gestiona la càmera
     */
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            // Configuració del cas d'ús Preview
            val previewUseCase = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Configuració del cas d'us imageCapture
            val imageCaptureUseCase: ImageCapture= ImageCapture.Builder().build()

            // Selecció de la càmera (posterior per defecte)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Desvincula abans de tornar a vincular (important per evitar errors)
            cameraProvider.unbindAll()

            // Vincula els casos d'ús al cicle de vida
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCaptureUseCase
            )
            // càmera vinculada correctament

        } catch (exc: Exception) {
            // Error al vincular casos d'us de la càmera
        }
    }, ContextCompat.getMainExecutor(context)) // Executa en el fil principal
}