package com.example.transformer.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Article
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.example.transformer.screen.WordToPdf.WordToPdfScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Transformer",fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding, modifier = Modifier.fillMaxSize()) {
                items(containerItems) { item ->
                ContainerItem(item,navController)

            }
        }
    }
}

@Composable
fun ContainerItem(item: ContainerItemData, navController: NavHostController) {
    val context = LocalContext.current

    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {

                if(item.heading=="Word to Pdf"){
                    val navigate = Intent(context, WordToPdfScreen::class.java)
                    context.startActivity(navigate)
                }

            },
            colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = item.heading,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class ContainerItemData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val heading: String,
    val description: String
)


val containerItems = listOf(
    ContainerItemData(Icons.Default.PhotoSizeSelectLarge, "Image Resize", "Adjust the dimensions of your images"),
    ContainerItemData(Icons.Default.PictureAsPdf, "Image To Pdf", "Convert images into PDF format"),
    ContainerItemData(Icons.Default.Image, "PDF to Image", "Extract images from PDF files"),
    ContainerItemData(Icons.Default.Description, "Word to Pdf", "Convert Word documents to PDF format"),
    ContainerItemData(Icons.Default.Article, "Pdf to Word", "Convert PDF files to editable Word documents")
)