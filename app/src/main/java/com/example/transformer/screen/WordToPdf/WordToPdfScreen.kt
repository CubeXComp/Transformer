package com.example.transformer.screen.WordToPdf

import DownloadButton
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.example.transformer.LayoutAndComposable.CollapsibleHeaderLayout
import com.example.transformer.LayoutAndComposable.ConvertBtn

import com.example.transformer.LayoutAndComposable.UploadContainerItem
import com.example.transformer.ui.theme.MotionLayoutWithNestedScrollAndSwipeableTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class WordToPdfScreen : ComponentActivity() {
    val viewModel by viewModels<WtpViewModel>()


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
                    PageView(viewModel)
                }
            }

        }
    }
}

@Composable
fun PageView(viewModel: WtpViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(contract = GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.FileUri = uri
            viewModel.FileName = viewModel.getFileNameFromUri(context, uri)
            Log.d("file:", "File Name:  ${viewModel.FileName}")

            viewModel.buttonText = viewModel.FileName
            viewModel.buttonTextDesc = "Change File"
        } ?: run {
            viewModel.FileName = "none"
            viewModel.FileUri = null
            viewModel.buttonText = "No File Selected"
            viewModel.buttonTextDesc = "Upload File"
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CollapsibleHeaderLayout(text = "Word To Pdf")

            UploadContainerItem(
                Icons.Default.PhotoSizeSelectLarge,
                viewModel.buttonText,
                viewModel.buttonTextDesc,
                {
                    filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                },
            )
        }

        // Column for bottom content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) {
            Button(
                onClick = {
                    filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(text = "Pick File")
            }

            // Show the "Convert To PDF" button if a file is selected
            if (viewModel.FileUri != null) {
                ConvertBtn(
                    onClick = {
                        if (viewModel.ConversionToPDFConditionCheck(context)) {
                            scope.launch(Dispatchers.IO) {
                                val result = viewModel.convertWordToPdf(
                                    context,
                                    viewModel.FileUri!!,
                                    viewModel.FileName
                                )
                                viewModel.pdfFileName = viewModel.FileName.replace(".docx", ".pdf")
                                viewModel.status = if (result) {
                                    viewModel.DownlaodBtn = true
                                    viewModel.pdfUri = viewModel.getPdfUri(context, viewModel.pdfFileName)
                                    "Conversion Successful"
                                } else {
                                    viewModel.DownlaodBtn = false
                                    viewModel.pdfUri = null
                                    "Conversion Failed"
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }

            // Show the download and share buttons if conversion is successful
            if (viewModel.DownlaodBtn) {
                Toast.makeText(context, "Conversion Successful", Toast.LENGTH_SHORT).show()
                Row {
                    DownloadButton(
                        icon = Icons.Default.Download,
                        onClick = { viewModel.saveToDevice(context, viewModel.pdfUri!!) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    DownloadButton(
                        icon = Icons.Default.Share,
                        onClick = { viewModel.pdfUri?.let { viewModel.downloadPdf(context, it, viewModel.pdfFileName) } }
                    )
                }
            }
        }
    }
}
