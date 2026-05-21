package com.example.seguimiento.features.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaFeedScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String, String, String, String, String) -> Unit,
    onNavigateToCreateWithCoords: (Double, Double) -> Unit
) {
    val mascotas by viewModel.mascotasMapa.collectAsState()
    val filtroCercania by viewModel.filtroCercania.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var mascotaSeleccionada by remember { mutableStateOf<Mascota?>(null) }
    
    var hasLocationPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val naranjaAppColor = Color(0xFFE67E22)
    val textoOscuroColor = Color(0xFF2E4053)
    val azulNav = Color(0xFF2196F3)

    val mapView = remember { 
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(6.5)
            controller.setCenter(GeoPoint(4.5709, -74.2973)) 
        }
    }

    val petFolder = remember { FolderOverlay() }
    val locationOverlay = remember(mapView) {
        val provider = GpsMyLocationProvider(context)
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER)
        provider.addLocationSource(LocationManager.GPS_PROVIDER)
        
        MyLocationNewOverlay(provider, mapView).apply {
            val bitmap = createRedDotBitmap(context)
            setPersonIcon(bitmap)
            setDirectionIcon(bitmap)
            setPersonAnchor(0.5f, 0.5f)
            setDirectionAnchor(0.5f, 0.5f)
            setDrawAccuracyEnabled(true)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        hasLocationPermission = granted
        if (granted) {
            locationOverlay.enableMyLocation()
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    loc?.let {
                        viewModel.updateUserLocation(it.latitude, it.longitude)
                        mapView.controller.animateTo(GeoPoint(it.latitude, it.longitude))
                        mapView.controller.setZoom(16.0)
                    }
                }
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            locationOverlay.enableMyLocation()
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    loc?.let { viewModel.updateUserLocation(it.latitude, it.longitude) }
                }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    if (hasLocationPermission) locationOverlay.enableMyLocation()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    locationOverlay.disableMyLocation()
                    mapView.onPause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = textoOscuroColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // BOTÓN FILTRO CERCANÍA
                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            viewModel.toggleFiltroCercania()
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    },
                    containerColor = if (filtroCercania) naranjaAppColor else Color.White,
                    contentColor = if (filtroCercania) Color.White else naranjaAppColor
                ) {
                    Icon(
                        imageVector = if (filtroCercania) Icons.Default.FilterAlt else Icons.Default.FilterAltOff, 
                        contentDescription = "Mascotas Cercanas"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            locationOverlay.enableMyLocation()
                            locationOverlay.enableFollowLocation()
                            
                            val myLoc = locationOverlay.myLocation
                            if (myLoc != null) {
                                viewModel.updateUserLocation(myLoc.latitude, myLoc.longitude)
                                mapView.controller.animateTo(myLoc)
                                mapView.controller.setZoom(18.5)
                            } else {
                                scope.launch { snackbarHostState.showSnackbar("Obteniendo ubicación...") }
                                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                                    .addOnSuccessListener { location ->
                                        location?.let {
                                            viewModel.updateUserLocation(it.latitude, it.longitude)
                                            val geo = GeoPoint(it.latitude, it.longitude)
                                            mapView.controller.animateTo(geo)
                                            mapView.controller.setZoom(18.5)
                                            mapView.invalidate()
                                        }
                                    }
                            }
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    },
                    containerColor = Color.White,
                    contentColor = azulNav
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Ubicación")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    mapView.apply {
                        overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                mascotaSeleccionada = null
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                p?.let { onNavigateToCreateWithCoords(it.latitude, it.longitude) }
                                return true
                            }
                        }))
                        overlays.add(petFolder)
                        overlays.add(locationOverlay)
                    }
                },
                update = { view ->
                    petFolder.items.clear()
                    mascotas.forEach { mascota ->
                        val marker = Marker(view).apply {
                            position = GeoPoint(mascota.lat, mascota.lng)
                            title = mascota.nombre
                            val miUbicacion = locationOverlay.myLocation
                            val estaCerca = if (miUbicacion != null) miUbicacion.distanceToAsDouble(position) < 100.0 else false
                            val colorInt = if (estaCerca) 0xFFE67E22.toInt() else {
                                when (mascota.estado) {
                                    PublicacionEstado.ADOPTADA -> 0xFF27AE60.toInt()
                                    PublicacionEstado.RECHAZADA -> 0xFFC0392B.toInt()
                                    else -> 0xFFF1C40F.toInt()
                                }
                            }
                            icon = createColoredMarker(context, colorInt, resaltar = estaCerca)
                            setOnMarkerClickListener { _, _ ->
                                mascotaSeleccionada = mascota
                                true
                            }
                        }
                        petFolder.add(marker)
                    }
                    
                    if (hasLocationPermission && !locationOverlay.isMyLocationEnabled) {
                        locationOverlay.enableMyLocation()
                    }
                    view.invalidate() 
                }
            )

            mascotaSeleccionada?.let { mascota ->
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            AsyncImage(
                                model = mascota.imagenUrl,
                                contentDescription = null,
                                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = mascota.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = textoOscuroColor)
                                Text(text = "${mascota.tipo} • ${mascota.edad}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = naranjaAppColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onNavigateToDetail(mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl) },
                            modifier = Modifier.fillMaxWidth().height(45.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = naranjaAppColor)
                        ) {
                            Text("VER DETALLES", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

private fun createRedDotBitmap(context: Context): Bitmap {
    val density = context.resources.displayMetrics.density
    val size = (32 * density).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, paint)
    paint.color = android.graphics.Color.RED
    canvas.drawCircle(size / 2f, size / 2f, size / 4.5f, paint)
    paint.color = android.graphics.Color.argb(150, 255, 255, 255)
    canvas.drawCircle(size / 2.2f, size / 2.2f, size / 12f, paint)
    return bitmap
}

private fun createColoredMarker(context: Context, colorInt: Int, resaltar: Boolean = false): Drawable? {
    val drawableId = if (resaltar) org.osmdroid.library.R.drawable.marker_default_focused_base else org.osmdroid.library.R.drawable.marker_default
    val drawable = ContextCompat.getDrawable(context, drawableId)
    return drawable?.let {
        val wrapped = it.mutate()
        wrapped.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
        wrapped
    }
}
