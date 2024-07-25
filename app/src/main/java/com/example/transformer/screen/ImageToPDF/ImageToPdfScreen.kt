package com.example.transformer.screen.ImageToPDF

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.transformer.ui.theme.MotionLayoutWithNestedScrollAndSwipeableTheme
import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ImageToPdfScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionLayoutWithNestedScrollAndSwipeableTheme  {
                Column {
                    Header()
                    ImageToPdfViews()
                }

            }
        }
    }
}

@Composable
fun ImageToPdfView() {

    LazyColumn(modifier = Modifier.fillMaxWidth(0.2f), horizontalAlignment = Alignment.CenterHorizontally)
    {
        item {


            Button(onClick = {} ) {
                Text(text = "Image To PDF",modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
            }
                Button(onClick = {  } ){
                    Text(text = "Download The PDF",modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)

            }

        }

    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageToPdfViews(viewModel: ItpViewModel = viewModel()) {
    val context = LocalContext.current
    val images = viewModel.images
    val showConvertButton by viewModel.showConvertButton
    val showDownloadButton by viewModel.showDownloadButton

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris?.let {
            uris.forEach { uri -> viewModel.addImage(uri) }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = bitmapToUri(context, bitmap)
            viewModel.addImage(uri)
        }
    }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Button(
            onClick = {
                if (cameraPermissionState.status.isGranted) {
                    cameraLauncher.launch()
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick from Camera")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick from Gallery")
        }

        if (images.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(images) { uri ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(100.dp)
                    ) {
                        Image(
                            bitmap = uriToBitmap(context, uri).asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                        IconButton(
                            onClick = { viewModel.removeImage(uri) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Delete")
                        }
                    }
                }
            }
        }

        if (showConvertButton) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.convertImagesToPdf() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Convert to PDF")
            }
        }

        if (showDownloadButton) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* Your logic to download the PDF */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Download The PDF")
            }
        }
    }
}

// Utility function to convert Bitmap to Uri
fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, UUID.randomUUID().toString() + ".png")
    try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
}

// Utility function to convert Uri to Bitmap
fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
}


@Composable
fun Header() {
    val scrollState = rememberScrollState()
    val height by animateDpAsState(targetValue = (300 - scrollState.value / 2).coerceIn(56, 300)
        .dp)
    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            ),


        contentAlignment = Alignment.BottomStart
    ) {
        Text(modifier = Modifier.padding(16.dp),text = "Image To PDF", fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
    }
}





