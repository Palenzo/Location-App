package blue.pink.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import blue.pink.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Myapp(viewModel = viewModel)
                }
            }
        }
    }
    @Composable
    fun Myapp(viewModel: LocationViewModel) {
        val context = LocalContext.current
        val LocationUtils = LocationUtils(context)
        LocationDisplay(locationUtils = LocationUtils, context = context, viewModel())
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun LocationDisplay(
        locationUtils: LocationUtils,
        context: Context,
        viewModel: LocationViewModel
    ){

        val location = viewModel.location.value
        val address = location?.let{
            locationUtils.reverseGeocodeLocation(location)
        }
        val requestpermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                permission->
                if (permission[Manifest.permission.ACCESS_COARSE_LOCATION]==true&&permission[Manifest.permission.ACCESS_FINE_LOCATION]==true){
                    //I have access
                    locationUtils.requestLocationUpdates(viewModel = viewModel)
                }else{
                    //Ask for permission
                    val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)|| ActivityCompat.shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        if (rationaleRequired){
                            Toast.makeText(context,
                                "Location is required to make feature work!",
                                Toast.LENGTH_LONG
                                ).show()
                        }else{
                            Toast.makeText(context,
                                "Go to the Settings, and enable!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        )
        
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (location!=null){
                Text(text = "Address - Latitude:${location.latitude}\n Longitude:${location.longitude}\n $address" )
            }else{
                Text(text = "Location not available")
            }
            Button(onClick = { /*TODO*/
                if (locationUtils.hasLocationPermission(context)){
                    //permission already granted
                    locationUtils.requestLocationUpdates(viewModel)
                }else{
                    //ask for permission
                    requestpermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }) {
                Text(text = "Get Location Access")
            }
        }
    }
}

