package com.example.transformer.screen.WordToPdf

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
                    CollapsibleHeaderScreen(viewModel)
                }
            }

        }
    }
}




@SuppressLint("SuspiciousIndentation")
@Composable
fun CollapsibleHeaderScreen(viewModel: WtpViewModel) {


    val context = LocalContext.current

    val scrollState = rememberScrollState()

    // For the Cart buttons


    // Header
    val headerHeight by animateDpAsState(targetValue = (300 - scrollState.value / 2).coerceIn(56, 300)
        .dp)

    // All Variables for Conversion to word to pdf

    val scope = rememberCoroutineScope()




    val filePickerLauncher = rememberLauncherForActivityResult(contract = GetContent()) { uri: Uri? ->
        uri?.let {

            if (uri != null) {
                viewModel.FileUri = uri
                viewModel.FileName = viewModel.getFileNameFromUri(context,uri)
                Log.d("file:" ,"File Name:  ${viewModel.FileName}}")

                viewModel.buttonText = "${viewModel.FileName}"
                viewModel.buttonTextDesc= "Change File"
            } else {
                viewModel.FileName = "none"
                viewModel.FileUri = null
                viewModel.buttonText = "No File Selected"
                viewModel.buttonTextDesc= "Upload File"
            }



        }
    }

        LazyColumn(modifier = Modifier.fillMaxWidth(0.2f), horizontalAlignment = Alignment.CenterHorizontally)
        {
            item {
                Header(headerHeight)
                UploadContainerItem(Icons.Default.PhotoSizeSelectLarge,  viewModel.buttonText,  viewModel.buttonTextDesc,
                    {
                        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    },)

                Button(
                    onClick = {
                        if( viewModel.ConversionToPDFConditionCheck(context)){
                            scope.launch(Dispatchers.IO) {
                                val result = viewModel.convertWordToPdf(context,  viewModel.FileUri!!, viewModel.FileName)
                                viewModel.pdfFileName = viewModel.FileName.replace(".docx", ".pdf")
                                viewModel.status = if (result) {
                                    viewModel.DownlaodBtn =true
                                    viewModel.pdfUri = viewModel.getPdfUri(context, viewModel.pdfFileName)
                                    "Conversion Successful"
                                } else {
                                    viewModel.DownlaodBtn =false
                                    viewModel.pdfUri = null
                                    "Conversion Failed"
                                }
                            }
                        }
                        else{
                            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
                        }
                    } ) {
                    Text(text = "Convert To PDF",modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
                }

                if(viewModel.DownlaodBtn){
                    Toast.makeText(context, "Conversion Successful", Toast.LENGTH_SHORT).show()
                    Button(onClick = { viewModel.pdfUri?.let { viewModel.downloadPdf(context, it, viewModel.pdfFileName) } } ){
                        Text(text = "Download The PDF",modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
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
        Text(modifier = Modifier.padding(16.dp),text = "Word To Pdf", fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
    }
}


