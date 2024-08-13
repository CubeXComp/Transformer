package com.example.transformer.screen.PdfToWord

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream

class PdfToWordViewModel : ViewModel() {
    var buttonText by mutableStateOf("Choose PDF File")
    var buttonTextDesc by mutableStateOf("Upload file")

    var FileName by mutableStateOf("none")
    var FileUri by mutableStateOf<Uri?>(null)
    var status by mutableStateOf("")
    var DownlaodBtn by mutableStateOf(false)
    var wordUri by mutableStateOf<Uri?>(null)
    var wordFileName by mutableStateOf("")

    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var fileName = "document.docx"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    fun convertPdfToWord(context: Context, uri: Uri, fileName: String): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)
            val wordDocument = XWPFDocument()
            val outputFile = File(context.cacheDir, fileName.replace(".pdf", ".docx"))
            val outputStream = FileOutputStream(outputFile)

            for (i in 1..pdfDocument.numberOfPages) {
                val pageContent = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i))
                val paragraph = wordDocument.createParagraph()
                val run = paragraph.createRun()
                run.setText(pageContent)
            }

            wordDocument.write(outputStream)
            outputStream.close()
            pdfDocument.close()
            pdfReader.close()
            inputStream?.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getWordUri(context: Context, fileName: String): Uri? {
        val wordFile = File(context.cacheDir, fileName)
        return if (wordFile.exists()) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", wordFile)
        } else {
            null
        }
    }

    fun downloadWord(context: Context, uri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        context.startActivity(Intent.createChooser(intent, "Download Word Document"))
    }

    fun ConversionToWordConditionCheck(context: Context): Boolean {
        if (FileUri == null && FileName == "none") {
            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
            return false
        } else if (buttonText == "Choose PDF File") {
            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
            return false
        } else if (buttonText == "No File Selected") {
            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
