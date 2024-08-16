package com.example.transformer.screen.PdfToImage
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.transformer.LayoutAndComposable.UploadContainerItem
import com.example.transformer.ui.theme.MotionLayoutWithNestedScrollAndSwipeableTheme
import androidx.compose.ui.graphics.asImageBitmap
import androidx.media3.exoplayer.offline.Download

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
                    PdfToImageView(viewModel);
                }



            }
        }
    }
}

@Composable
fun PdfToImageView(viewModel: PdfToImageViewModel) {

    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val headerHeight by animateDpAsState(targetValue = (300 - scrollState.value / 2).coerceIn(56, 300)
        .dp)
    // All Variables for Conversion to word to pdf
    val scope = rememberCoroutineScope()

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
//            onPdfSelected(data?.data)
            viewModel.PdfFileUri = data?.data;
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(0.2f),
        horizontalAlignment = Alignment.CenterHorizontally

    )


    {

        item {
            Header()
            UploadContainerItem(
                Icons.Default.PhotoSizeSelectLarge,  viewModel.buttonText,  viewModel.buttonTextDesc,
                {
                    viewModel.ShowImages.value = false
                    viewModel.DownlaodBtn = false;
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/pdf"
                    }
                    pdfPickerLauncher.launch(intent)
                },)

            if(viewModel.PdfFileUri != null){
                Button(onClick = {

                    val PdfFile = viewModel.uriToFile(viewModel.PdfFileUri!!,context)
                    viewModel.convertPdfToImages(PdfFile)

                    Log.d("PDF ARRAY", viewModel.ImagesUriList[0].toString())

                } ){
                    Text(text = "Convert the PDF",modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
                }
            }

            if(viewModel.ShowImages.value){
                ImageView(viewModel)
                viewModel.DownlaodBtn = true;

            }

            if(viewModel.DownlaodBtn){
                Toast.makeText(context, "Conversion Successful", Toast.LENGTH_SHORT).show()
                Button(onClick = {viewModel.saveImagesToGallery(context)}){
                    Text(text = "Save Images",modifier = Modifier.padding(16.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
                }
            }



        }
    }
}

@Composable
fun ImageView(viewModel: PdfToImageViewModel) {
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
                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null,
                    modifier = Modifier
                        .width(200.dp)
                        .height(300.dp) // Adjust size as needed
                        .padding(4.dp)
                )
            }
        }
    }
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
        Text(modifier = Modifier.padding(16.dp),text = "Pdf To Image", fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)
    }
}


