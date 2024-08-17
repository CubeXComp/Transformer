package com.example.transformer.screen.HomePage


import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel

class HomeScreenViewModel : ViewModel() {
    fun shareApp(context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this awesome app: [App Link]")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}