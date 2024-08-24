import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Transform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    Scaffold(
//        topBar = {
//            LargeTopAppBar(
//                title = {
//                    Text(
//                        "Transformer",
//                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
//                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
//                    )
//                },
//                colors = TopAppBarDefaults.largeTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            )
//        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Transform,
                contentDescription = "Transformer Icon",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Welcome to Transformer",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Transform your documents and images with ease",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Get Started", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}