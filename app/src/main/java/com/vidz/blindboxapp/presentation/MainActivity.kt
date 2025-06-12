package com.vidz.blindboxapp.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vidz.blindboxapp.presentation.app.BlindBoxApp
import com.vidz.blindboxapp.worker.CartNotificationWorker
import com.vidz.theme.BlindBoxTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var workManager: WorkManager
    
    private var shouldNavigateToCart = false
    private var isFromNotification = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check if we should navigate to cart from notification
        checkNavigationIntent(intent)
        
        // Only enqueue cart notification worker if NOT coming from notification
        if (!isFromNotification) {
            enqueueCartNotificationWorker()
        }
        
        setContent {
            BlindBoxApp(
                shouldNavigateToCart = shouldNavigateToCart,
                isFromNotification = isFromNotification
            )
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Important: update the intent
        checkNavigationIntent(intent)
        
        // Update the compose content with new navigation state
        setContent {
            BlindBoxApp(
                shouldNavigateToCart = shouldNavigateToCart,
                isFromNotification = isFromNotification
            )
        }
    }
    
    private fun checkNavigationIntent(intent: Intent) {
        shouldNavigateToCart = intent.getBooleanExtra(CartNotificationWorker.EXTRA_NAVIGATE_TO_CART, false)
        isFromNotification = intent.getBooleanExtra(CartNotificationWorker.EXTRA_FROM_NOTIFICATION, false)
        
        // Clear the intent extras to prevent repeated navigation
        if (shouldNavigateToCart) {
            intent.removeExtra(CartNotificationWorker.EXTRA_NAVIGATE_TO_CART)
            intent.removeExtra(CartNotificationWorker.EXTRA_FROM_NOTIFICATION)
        }
    }
    
    private fun enqueueCartNotificationWorker() {
        lifecycleScope.launch {
            // Add a longer delay for better user experience
            delay(5000) // 5 seconds delay
            
            val cartNotificationWork = OneTimeWorkRequestBuilder<CartNotificationWorker>()
                .build()
            
            workManager.enqueue(cartNotificationWork)
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlindBoxTheme {
        Greeting("Android")
    }
}