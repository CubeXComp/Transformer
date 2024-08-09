package com.example.transformer.screen.PdfToImage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PdfToImageViewModel : ViewModel() {

    var PdfFileUri by mutableStateOf<Uri?>(null)
    var buttonText by mutableStateOf("Choose PDF File")
    var buttonTextDesc by mutableStateOf("Upload file")

    val ImagesUriList = mutableStateListOf<Bitmap>()

    val ShowImages = mutableStateOf(false)




    fun convertPdfToImages(pdfFile: File): Unit {
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val pageCount = pdfRenderer.pageCount
        ImagesUriList.clear()
        for (i in 0 until pageCount) {
            val page = pdfRenderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            ImagesUriList.add(bitmap)
            page.close()
        }

        pdfRenderer.close()
        fileDescriptor.close()
        ShowImages.value = true

    }

    fun uriToFile(uri: Uri, context: Context): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "selected_pdf.pdf")
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

}