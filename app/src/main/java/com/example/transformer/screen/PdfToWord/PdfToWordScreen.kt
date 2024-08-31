package com.example.transformer.screen.PdfToWord

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.magnifier
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.example.transformer.LayoutAndComposable.CollapsibleHeaderLayout
import com.example.transformer.LayoutAndComposable.ConvertBtn
import com.example.transformer.LayoutAndComposable.CustomLoading
import com.example.transformer.LayoutAndComposable.UploadContainerItem
import com.example.transformer.ui.theme.MotionLayoutWithNestedScrollAndSwipeableTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PdfToWordScreen : ComponentActivity() {
    val viewModel by viewModels<PdfToWordViewModel>()

    @OptIn(ExperimentalWearMaterialApi::class, ExperimentalMotionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionLayoutWithNestedScrollAndSwipeableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CollapsibleHeaderScreen(viewModel)
                }
            }
        }
    }
}
@Composable
fun CollapsibleHeaderScreen(viewModel: PdfToWordViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // UI State variables
    val fileSelected = viewModel.FileUri != null
    val conversionDone = viewModel.DownlaodBtn
    val isConverting = remember { mutableStateOf(false) } // Track conversion state

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(contract = GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.FileUri = uri
            viewModel.FileName = viewModel.getFileNameFromUri(context, uri)
            Log.d("file:", "File Name: ${viewModel.FileName}")
            viewModel.buttonText = viewModel.FileName ?: "No File Selected"
            viewModel.buttonTextDesc = if (uri != null) "Change File" else "Upload File"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                ,horizontalAlignment = Alignment.CenterHorizontally

        ) {
            CollapsibleHeaderLayout(text = "Pdf To Word")
            UploadContainerItem(
                icon = Icons.Default.PhotoSizeSelectLarge,
                heading = viewModel.buttonText,
                description = viewModel.buttonTextDesc
            ) {
                filePickerLauncher.launch("application/pdf")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(color = Color.Red)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Show the "Pick File" button if no file is selected

            if(viewModel.FileUri == null) {
                Button(modifier = Modifier.padding(horizontal = 16.dp), onClick = {
                    filePickerLauncher.launch("application/pdf")
                }) {
                    Icon(imageVector = Icons.Default.FileOpen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Pick a File")
                }
            }

            // Show the "Convert" button if a file is selected but not yet converted
            if (fileSelected && !conversionDone && !isConverting.value) {
                ConvertBtn(
                    convertIcon = Icons.Default.Sync,
                    fileIcon = Icons.Default.FileDownload,
                    onClick = {
                        if (viewModel.ConversionToWordConditionCheck(context)) {
                            isConverting.value = true // Set conversion state to true
                            scope.launch(Dispatchers.IO) {
                                val result = viewModel.convertPdfToWord(
                                    context,
                                    viewModel.FileUri!!,
                                    viewModel.FileName
                                )
                                viewModel.wordFileName = viewModel.FileName.replace(".pdf", ".docx")
                                viewModel.status = if (result) {
                                    viewModel.DownlaodBtn = true
                                    viewModel.wordUri =
                                        viewModel.getWordUri(context, viewModel.wordFileName)
                                    "Conversion Successful"
                                } else {
                                    viewModel.DownlaodBtn = false
                                    viewModel.wordUri = null
                                    "Conversion Failed"
                                }
                                isConverting.value = false // Set conversion state to false
                            }
                        } else {
                            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Show the "Download" button if conversion is done
            if (conversionDone) {
                Button(onClick = {
                    viewModel.wordUri?.let {
                        viewModel.shareFile(context,
                            it,
                            viewModel.wordFileName
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Share the Word Document")
                }
            }
        }

        // Show loading indicator if conversion is in progress
        if (isConverting.value) {
            CustomLoading()
        }
    }
}

