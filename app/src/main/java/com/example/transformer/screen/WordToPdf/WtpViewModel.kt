package com.example.transformer.screen.WordToPdf
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import org.apache.poi.xwpf.usermodel.XWPFDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.InputStream


class WtpViewModel : ViewModel() {


    var buttonText by mutableStateOf("Choose Word File")
    var buttonTextDesc by mutableStateOf("Upload file")

    var FileName by mutableStateOf("none")
    var FileUri by mutableStateOf<Uri?>(null)
    var status by mutableStateOf("")
    var DownlaodBtn by mutableStateOf(false)
    var pdfUri by mutableStateOf<Uri?>(null)
    var pdfFileName by mutableStateOf("")



    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var fileName = "document.pdf"
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

    fun convertWordToPdf(context: Context, uri: Uri, fileName: String): Boolean {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val document = XWPFDocument(inputStream)
            val outputFile = File(context.cacheDir, fileName.replace(".docx", ".pdf"))
            val pdfWriter = PdfWriter(outputFile)
            val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
            val documentPdf = Document(pdfDoc)

            document.paragraphs.forEach { paragraph ->
                documentPdf.add(Paragraph(paragraph.text))
            }

            documentPdf.close()
            pdfDoc.close()
            pdfWriter.close()
            inputStream?.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getPdfUri(context: Context, fileName: String): Uri? {
        val pdfFile = File(context.cacheDir, fileName)
        return if (pdfFile.exists()) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        } else {
            null
        }
    }

    fun downloadPdf(context: Context, uri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        context.startActivity(Intent.createChooser(intent, "Download PDF"))
    }



    fun ConversionToPDFConditionCheck(context: Context):Boolean{
        if(FileUri == null && FileName == "none"){
            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
            return false;
        }
        else if( buttonText == "Choose Word File"){
            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
            return false;
        }
        else if( buttonText == "No File Selected" ){
            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()
            return false;
        }
        return true;
    }

}



