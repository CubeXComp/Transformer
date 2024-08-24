package com.example.transformer

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.common.io.Files.getFileExtension
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utility {

    object TestAds {
        const val BANNER_AD = "ca-app-pub-3940256099942544/9214589741"
        const val INTERSTITIAL_AD = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED_AD = "ca-app-pub-3940256099942544/5224354917"
        const val REWARDED_INTERSTITIAL_AD = "ca-app-pub-3940256099942544/5354046379"
        
    }


    fun saveFileToCustomFolder(context: Context, uri: Uri, fileName: String) {
        var fileName = fileName

        // If fileName is empty, generate a new file name based on the current date and time
        if (fileName.isEmpty()) {
            fileName = generateFileName()
        }

        // Create a custom directory in the main root of the internal storage
        val customFolder = File(Environment.getExternalStorageDirectory(), "Transformer")

        if (!customFolder.exists()) {
            val isDirCreated = customFolder.mkdirs() // Create the directory if it doesn't exist
            Log.d("Directory Creation", "Directory created: $isDirCreated")
        }

        Log.d("File Path", customFolder.path.toString())

        val fileExtension = getFileExtension(context, uri)

        // Create a file in the custom directory with the correct extension
        val file = File(customFolder, "$fileName$fileExtension")

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.close()
            inputStream?.close()
            Toast.makeText(context, "File saved: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Issue while saving", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun getFileExtension(context: Context, uri: Uri): String {
        var extension: String? = null

        // Try to get the file extension from the ContentResolver
        val mimeType = context.contentResolver.getType(uri)
        if (mimeType != null) {
            val mime = android.webkit.MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(mimeType)
        }

        // If MIME type doesn't provide extension, try to get it from the URI
        if (extension == null) {
            extension = uri.path?.let { path ->
                val dotIndex = path.lastIndexOf(".")
                if (dotIndex != -1) {
                    path.substring(dotIndex)
                } else {
                    ""
                }
            }
        }

        return if (extension.isNullOrEmpty()) "" else ".$extension"
    }
    fun generateFileName(): String {
        // Define the date format you want to use for the file name
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDateAndTime: String = dateFormat.format(Date())
        return "File_$currentDateAndTime"
    }
}