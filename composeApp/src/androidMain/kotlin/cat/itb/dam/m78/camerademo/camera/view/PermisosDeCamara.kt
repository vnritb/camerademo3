package cat.itb.dam.m78.camerademo.camera.view

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat

@Composable
fun permisosDeCamera(context: Context,granted: (Boolean) -> Unit
){
    //Objecte per gestionar el permís.  Llença la finestra de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = granted // Callback que s'executa quan l'usuari respon a la sol·licitud de permís
    )

    // Demanar permís quan el composable entra a la composició si encara no s'ha concedit
    LaunchedEffect(key1 = true) { // S'executa només una vegada a l'inici
        //Pot ser que ja estigui concedit (si es va seleccionar l'opció de permetre sempre)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            //En cas de no tenir permís (la primera vegada), es llença la finestra de permisos
        }
    }
}