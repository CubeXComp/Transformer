package com.example.transformer.LayoutAndComposable

import DownloadButton
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomMenu(fileConversionStatus: Uri,fileDownloadStatus: Boolean, onConvertClick: () -> Unit, onDownloadClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Optional padding for the buttons
    ) {
        Row(
            modifier = Modifier.align(Alignment.BottomCenter), // Aligns the buttons to the bottom center
        ) {
            // Convert Button only if a file is selected
            if (fileConversionStatus != null) {
                ConvertBtn(
                    onClick = onConvertClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Spacing between the two buttons

            // Download Button only if conversion is successful
            if (fileDownloadStatus) {
                DownloadButton(
                    onClick = onDownloadClick
                )
            }
        }
    }
}
