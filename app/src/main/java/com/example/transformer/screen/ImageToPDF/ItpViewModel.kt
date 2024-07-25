package com.example.transformer.screen.ImageToPDF
import android.app.Application
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class ItpViewModel(application: Application) : AndroidViewModel(application) {
    val images: SnapshotStateList<Uri> = mutableStateListOf()
    val showConvertButton = mutableStateOf(false)
    val showDownloadButton = mutableStateOf(false)
    val pdfUri = mutableStateOf("")
    val currPage = mutableStateOf(null)

//    private fun loadPdfPages(){
//
//        val file = Uri.parse(pdfUri.toString()).path?.let { File(it) }
//        val executorService = Executors.newSingleThreadExecutor()
//        val handler = Handler(Looper.getMainLooper())
//        executorService.execute {
//            try{
//                val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
//                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
//                val pageCount = pdfRenderer.pageCount
//                if(pageCount <=0) {
//                    Log.d("TAG", "loadThumbnailFromPdf: No pages")
//                }
//                else {
//                    Log.d("TAG", "loadThumbnailFromPdf: $pageCount")
//                    for(i in 0 until pageCount) {
//                        // close current page before opening new page
//                        if(currPage!=null){
//                            currPage?.close()
//                        }
//                        currPage = pdfRenderer.openPage(i)
//                        val bitmap = Bitmap.createBitmap(
//                            currPage!!.width,
//                            currPage!!.height,
//                            Bitmap.Config.ARGB_8888
//                        )
//
//                        currPage!!.render(bitmap,null,null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//                        val pdfViewModel = PdfViewModel(Uri.parse(pdfUri), (i+1), pageCount, bitmap)
//                        pdfViewArrayList.add(pdfViewModel)
//                    }
//                }
//            }
//            catch(e:Exception) {
//
//            }
//        }
//
//    }

    fun convertPicturesToPdf(uriList: List<Uri>, convertAll: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val picturesToPdfList = if (convertAll) {
                uriList
            } else {
                uriList.filter { /* your condition for selecting specific URIs */ true }
            }

            try {
                val root = File(context.getExternalFilesDir(null), "PDF_FOLDER")
                root.mkdirs()
                val timestamp = System.currentTimeMillis()
                val fileName = "PDF_$timestamp.pdf"
                val file = File(root, fileName)
                val fileOutputStream = FileOutputStream(file)
                val pdfDocument = PdfDocument()
                var cnt = 0

                picturesToPdfList.forEach { pictureUri ->
                    try {
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, pictureUri))
                        } else {
                            @Suppress("DEPRECATION")
                            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, pictureUri)
                        }
                        val copiedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                        cnt += 1
                        val pageInfo = PdfDocument.PageInfo.Builder(copiedBitmap.width, copiedBitmap.height, cnt).create()
                        val page = pdfDocument.startPage(pageInfo)
                        val canvas = page.canvas
                        canvas.drawBitmap(copiedBitmap, 0f, 0f, null)
                        pdfDocument.finishPage(page)
                        copiedBitmap.recycle()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                pdfDocument.writeTo(fileOutputStream)
                pdfDocument.close()
                fileOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            onComplete()
        }
    }



    fun addImage(uri: Uri) {
        images.add(uri)
        showConvertButton.value = true
    }

    fun removeImage(uri: Uri) {
        images.remove(uri)
        if (images.isEmpty()) {
            showConvertButton.value = false
            showDownloadButton.value = false
        }
    }

    fun convertImagesToPdf() {
        // Add your PDF conversion logic here
        showDownloadButton.value = true
    }
}