package com.example.transformer.screen.WordToPdf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.example.transformer.LayoutAndComposable.UploadContainerItem
import com.example.transformer.screen.ContainerItem
import com.example.transformer.ui.theme.MotionLayoutWithNestedScrollAndSwipeableTheme


class WordToPdfScreen : ComponentActivity() {
    @OptIn(ExperimentalWearMaterialApi::class, ExperimentalMotionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionLayoutWithNestedScrollAndSwipeableTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CollapsibleHeaderScreen()
                }
            }

        }
    }
}
@Composable
fun CollapsibleHeaderScreen() {
    val scrollState = rememberScrollState()
    val headerHeight by animateDpAsState(targetValue = (300 - scrollState.value / 2).coerceIn(56, 300).dp)


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Header(headerHeight)

                UploadContainerItem(
                    Icons.Default.PhotoSizeSelectLarge,
                    "Choose Word File",
                    "Upload",
                    {
                        //  Image Picker
                    },
                )
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

@Composable
fun TextItem(index: Int) {
    Text(
        text = "Item #$index",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MotionLayoutWithNestedScrollAndSwipeableTheme {
        CollapsibleHeaderScreen()
    }
}