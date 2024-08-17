package com.example.transformer.screen.HomePage
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.transformer.Utility
import com.example.transformer.screen.PdfToImage.PdfToImageScreen
import com.example.transformer.screen.PdfToWord.PdfToWordScreen
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Transformer", fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            item { CloudStorageStatus() }
            item { QuickAccess(context,navController) }
            item { GoPremiumButton() }
            item { ShareWithFriends() }
            item { RecentlyOpened() }
        }
    }
}

@Composable
fun CloudStorageStatus() {
    val context = LocalContext.current
    val storageInfo = remember { getStorageInfo(context) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ,elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Device Storage", style = MaterialTheme.typography.titleMedium)
            Text(
                "${storageInfo.usedSpace} of ${storageInfo.totalSpace} used",
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = storageInfo.usedPercentage.toFloat() / 100,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Text(
                "${storageInfo.freeSpace} available",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

data class StorageInfo(
    val totalSpace: String,
    val usedSpace: String,
    val freeSpace: String,
    val usedPercentage: Int
)

fun getStorageInfo(context: Context): StorageInfo {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val totalBytes = stat.totalBytes
    val freeBytes = stat.freeBytes
    val usedBytes = totalBytes - freeBytes

    val df = DecimalFormat("#.##")
    val totalGB = df.format(totalBytes.toDouble() / (1024 * 1024 * 1024))
    val usedGB = df.format(usedBytes.toDouble() / (1024 * 1024 * 1024))
    val freeGB = df.format(freeBytes.toDouble() / (1024 * 1024 * 1024))

    val usedPercentage = ((usedBytes.toDouble() / totalBytes.toDouble()) * 100).toInt()

    return StorageInfo(
        totalSpace = "${totalGB} GB",
        usedSpace = "${usedGB} GB",
        freeSpace = "${freeGB} GB",
        usedPercentage = usedPercentage
    )
}

@Composable
fun QuickAccess(context: Context,navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Quick access", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickAccessItem(
                icon = Icons.Default.PictureAsPdf,
                title = "PDF to IMG",
                onClick = {
                    val navigate = Intent(context, PdfToImageScreen::class.java)
                    context.startActivity(navigate)
                }
            )
            QuickAccessItem(
                icon = Icons.Default.Description,
                title = "PDF to WORD",
                onClick = {
                    val navigate = Intent(context, PdfToWordScreen::class.java)
                    context.startActivity(navigate)
                }
            )
            QuickAccessItem(
                icon = Icons.Default.MoreHoriz,
                title = "View More",
                onClick = {
                    navController.navigate("Tool")
                }
            )
        }
    }
}

@Composable
fun QuickAccessItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GoPremiumButton() {
    Column {
        Button(
            onClick = { /* Handle click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Go Premium")
            Spacer(Modifier.weight(1f))
            Text("Upgrade")
        }
        Box(modifier = Modifier.padding(12.dp)) {
            BannerAds()
        }
    }

}
@Composable
fun BannerAds(){
    AndroidView(
        modifier = Modifier.fillMaxWidth(), factory = {context->
            AdView(context).apply{
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                context,AdSize.FULL_WIDTH
            ))
                adUnitId = Utility.TestAds.BANNER_AD
                loadAd(AdRequest.Builder().build())
        }}
    )

}

@Composable
fun ShareWithFriends(viewModel: HomeScreenViewModel = HomeScreenViewModel()) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Share with friends", style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    viewModel.shareApp(context)
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Share this app", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun RecentlyOpened() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recently opened", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { /* Handle click */ }) {
                Text("See all")
            }
        }
        // Add list of recently opened files here
    }
}