package com.example.transformer.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.transformer.screen.ToolPage.Tool
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class ToolsViewModel : ViewModel() {
    private val _tools = mutableStateOf<List<Tool>>(emptyList())
    val tools: State<List<Tool>> = _tools

    init {
        loadTools()
    }

    private fun loadTools() {
        viewModelScope.launch {
            _tools.value = listOf(
                Tool(Icons.Filled.PictureAsPdf, "PDF to Image", "Convert PDF files to images."),
                Tool(Icons.Filled.PictureAsPdf, "PDF to Word", "Convert PDF files to Word documents."),
                Tool(Icons.Filled.Image, "Image Resize", "Resize your images to the desired dimensions."),
                Tool(Icons.Filled.PictureAsPdf, "Image to PDF", "Convert images to PDF files."),
                Tool(Icons.Filled.PictureAsPdf, "Word to PDF", "Convert Word documents to PDF files.")
            )
        }
    }
}
