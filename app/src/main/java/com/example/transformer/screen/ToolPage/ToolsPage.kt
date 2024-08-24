package com.example.transformer.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.transformer.LayoutAndComposable.CollapsibleHeaderLayout
import com.example.transformer.screen.ImageToPDF.ImageToPdfScreen
import com.example.transformer.screen.PdfToImage.PdfToImageScreen
import com.example.transformer.screen.PdfToWord.PdfToWordScreen
import com.example.transformer.viewmodel.ToolsViewModel
import com.example.transformer.screen.ToolPage.Tool
import com.example.transformer.screen.WordToPdf.WordToPdfScreen

@Composable
fun ToolsPage(toolsViewModel: ToolsViewModel = viewModel()) {
    val tools = toolsViewModel.tools.value
    val context = LocalContext.current as Activity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {
        Column {
            CollapsibleHeaderLayout(text = "Tools Screen")
            Column {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tools.size) { index ->
                        ToolCard(tool = tools[index], context = context)
                    }
                }
            }
        }

    }
}

@Composable
fun ToolCard(tool: Tool, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Handle navigation
                when (tool.label) {
                    "PDF to Image" -> context.startActivity(
                        Intent(
                            context,
                            PdfToImageScreen::class.java
                        )
                    )

                    "PDF to Word" -> context.startActivity(
                        Intent(
                            context,
                            PdfToWordScreen::class.java
                        )
                    )
//                    "Image Resize" -> context.startActivity(Intent(context, ::class.java))
                    "Image to PDF" -> context.startActivity(
                        Intent(
                            context,
                            ImageToPdfScreen::class.java
                        )
                    )

                    "Word to PDF" -> context.startActivity(
                        Intent(
                            context,
                            WordToPdfScreen::class.java
                        )
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.label,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = tool.label,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToolsPagePreview() {
    ToolsPage()
}
