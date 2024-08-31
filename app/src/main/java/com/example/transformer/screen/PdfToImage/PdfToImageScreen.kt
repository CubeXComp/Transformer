package com.example.transformer.screen.PdfToImage

import DownloadButton
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.transformer.LayoutAndComposable.CollapsibleHeaderLayout
import com.example.transformer.LayoutAndComposable.ConvertBtn
import com.example.transformer.LayoutAndComposable.CustomLoading
import com.example.transformer.ui.theme.MotionLayoutWithNestedScrollAndSwipeableTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.transformer.LayoutAndComposable.UploadContainerItem

class PdfToImageScreen : ComponentActivity() {
    val viewModel by viewModels<PdfToImageViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionLayoutWithNestedScrollAndSwipeableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PdfToImagePageView(viewModel)
                }
            }
        }
    }
}

@Composable
fun PdfToImagePageView(viewModel: PdfToImageViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val headerHeight by animateDpAsState(
        targetValue = (300 - scrollState.value / 2).coerceIn(56, 300).dp
    )
    val scope = rememberCoroutineScope()

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            viewModel.PdfFileUri = data?.data
        }
    }



    Box(modifier = Modifier.fillMaxSize(),) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 80.dp), // Adjusted for bottom area
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CollapsibleHeaderLayout(text = "Pdf To Image")
            UploadContainerItem(
                icon = Icons.Default.PhotoSizeSelectLarge,
                heading = viewModel.buttonText,
                description = viewModel.buttonTextDesc,
                onClick = {
                    viewModel.ShowImages.value = false
                    viewModel.DownlaodBtn = false
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/pdf"
                    }
                    pdfPickerLauncher.launch(intent)
                }
            )

            // Display loading indicator while converting
            if (viewModel.isConverting) {
                CustomLoading()
            }

            if (viewModel.ShowImages.value) {
                PdfImagesView(viewModel)
                viewModel.DownlaodBtn = true
            }
        }

        BottomArea(
            viewModel = viewModel,
            onConvertClick = { viewModel.convertPdfToImages(viewModel.uriToFile(viewModel.PdfFileUri!!, context)) },
            onDownloadClick = { viewModel.saveImagesToGallery(context) }
        )
    }
}

@Composable
fun BottomArea(viewModel: PdfToImageViewModel, onConvertClick: () -> Unit, onDownloadClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Optional padding for the buttons
    ) {
        Row(
            modifier = Modifier.align(Alignment.BottomCenter), // Aligns the buttons to the bottom center
        ) {
            // Convert Button only if a file is selected
            if (viewModel.PdfFileUri != null) {
                ConvertBtn(
                    onClick = onConvertClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Spacing between the two buttons

            // Download Button only if conversion is successful
            if (viewModel.DownlaodBtn) {
                DownloadButton(
                    onClick = onDownloadClick
                )
            }
        }
    }
}

@Composable
fun PdfImagesView(viewModel: PdfToImageViewModel) {
    val images by remember { mutableStateOf(viewModel.ImagesUriList) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = viewModel.buttonTextDesc, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images) { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(), contentDescription = null,
                    modifier = Modifier
                        .width(200.dp)
                        .height(300.dp)
                        .padding(4.dp)
                )
            }
        }
    }
}
