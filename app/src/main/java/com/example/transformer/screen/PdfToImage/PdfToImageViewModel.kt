package com.example.transformer.screen.PdfToImage

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class PdfToImageViewModel : ViewModel() {


    var PdfFileUri by mutableStateOf<Uri?>(null)
    var buttonText by mutableStateOf("Choose PDF File")
    var buttonTextDesc by mutableStateOf("Upload file")
    var DownlaodBtn by mutableStateOf(false)

    var isConverting by mutableStateOf(false)
    val ImagesUriList = mutableStateListOf<Bitmap>()

    val ShowImages = mutableStateOf(false)

    fun convertPdfToImages(pdfFile: File): Unit {
        isConverting = true
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

    fun shareImages(context: Context) {
        val imageUris = ArrayList<Uri>()

        ImagesUriList.forEachIndexed { index, bitmap ->
            val fileName = "PDF_Image_$index.jpg"
            val file = File(context.cacheDir, fileName)
            val fos = FileOutputStream(file)

            fos.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            imageUris.add(uri)
        }

        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/jpeg"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Images"))
    }
    fun saveImagesToGallery(context: Context) {
        ImagesUriList.forEachIndexed { index, bitmap ->
            val fileName = "PDF_Image_$index.jpg"
            var fos: OutputStream? = null
            var success = true

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10 and above
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        fos = resolver.openOutputStream(imageUri)
                    } else {
                        success = false
                        Toast.makeText(context, "Failed to create image Uri for $fileName", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // For Android versions below 10
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = File(imagesDir, fileName)
                    fos = FileOutputStream(image)
                }

                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    Toast.makeText(context, "$fileName saved successfully!", Toast.LENGTH_SHORT).show()
                } ?: run {
                    success = false
                    Toast.makeText(context, "Failed to save $fileName", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                success = false
                Toast.makeText(context, "Error saving $fileName: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                fos?.close()
            }

            if (!success) {
                Toast.makeText(context, "Failed to save some images.", Toast.LENGTH_SHORT).show()
                return
            }
        }
    }


}