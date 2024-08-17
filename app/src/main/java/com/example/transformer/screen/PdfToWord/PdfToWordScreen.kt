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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun CollapsibleHeaderScreen(viewModel: PdfToWordViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val headerHeight by animateDpAsState(targetValue = (300 - scrollState.value / 2).coerceIn(56, 300).dp)
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(contract = GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.FileUri = uri
            viewModel.FileName = viewModel.getFileNameFromUri(context, uri)
            Log.d("file:", "File Name:  ${viewModel.FileName}")
            viewModel.buttonText = viewModel.FileName ?: "No File Selected"
            viewModel.buttonTextDesc = if (uri != null) "Change File" else "Upload File"
        }
    }

    LazyColumn(modifier = Modifier.fillMaxWidth(0.2f), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Header(headerHeight)
            UploadContainerItem(Icons.Default.PhotoSizeSelectLarge, viewModel.buttonText, viewModel.buttonTextDesc) {
                filePickerLauncher.launch("application/pdf")
            }
            Button(
                onClick = {
                    if (viewModel.ConversionToWordConditionCheck(context)) {
                        scope.launch(Dispatchers.IO) {
                            val result = viewModel.convertPdfToWord(context, viewModel.FileUri!!, viewModel.FileName)
                            viewModel.wordFileName = viewModel.FileName.replace(".pdf", ".docx")
                            viewModel.status = if (result) {
                                viewModel.DownlaodBtn = true
                                viewModel.wordUri = viewModel.getWordUri(context, viewModel.wordFileName)
                                "Conversion Successful"
                            } else {
                                viewModel.DownlaodBtn = false
                                viewModel.wordUri = null
                                "Conversion Failed"
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = "Convert To Word", modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
            }
            if (viewModel.DownlaodBtn) {
                Toast.makeText(context, "Conversion Successful", Toast.LENGTH_SHORT).show()
                Button(onClick = { viewModel.wordUri?.let { viewModel.downloadWord(context, it, viewModel.wordFileName) } }) {
                    Text(text = "Download The Word Document", modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
                }
            }
        }
    }
}

@Composable
fun Header(height: Dp) {
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
        Text(modifier = Modifier.padding(16.dp), text = "PDF To Word", fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
    }
}
