package com.vidz.blindboxapp.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vidz.base.components.NotificationBannerManager
import com.vidz.blindboxapp.R
import com.vidz.blindboxapp.presentation.MainActivity
import com.vidz.domain.usecase.ObserveCartItemsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class CartNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val observeCartItemsUseCase: ObserveCartItemsUseCase
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "cart_notifications"
        const val CHANNEL_NAME = "Cart Notifications"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_NAVIGATE_TO_CART = "navigate_to_cart"
        const val EXTRA_FROM_NOTIFICATION = "from_notification"
        
        private val randomMessages = listOf(
            "Your favorite items are waiting! 🛒",
            "Special blind boxes in your cart! 📦", 
            "Complete your order for exclusive deals! ✨",
            "Amazing items are waiting for you! 🎁",
            "Your cart misses you! Come back! 💝",
            "Exclusive products waiting for checkout! 🌟",
            "Don't miss out on your selected items! 🔥",
            "Your blind box adventure awaits! 🎉"
        )
    }

    override suspend fun doWork(): Result {
        return try {
            // Initialize notification channels
            NotificationBannerManager.initializeNotificationChannels(applicationContext)
            
            // Get current cart items
            val cartItems = observeCartItemsUseCase().first()
            
            if (cartItems.isNotEmpty()) {
                // Send notification banner with Shopee-style messaging
                sendShopeeStyleCartNotification(cartItems.size)
            }
            
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }

    private fun sendShopeeStyleCartNotification(itemCount: Int) {
        // Create intent with proper flags to prevent recreation
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            // Use SINGLE_TOP to prevent creating new activity instance
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO_CART, true)
            putExtra(EXTRA_FROM_NOTIFICATION, true)
            
            // Add action to make intent unique and prevent conflicts
            action = "com.vidz.blindboxapp.CART_NOTIFICATION_${System.currentTimeMillis()}"
        }
        
        // Get random Shopee-style message
        val randomMessage = randomMessages.random()
        
        // Shopee-style title and content
        val title = "🛒 BlindBox - Your Shopping Cart"
        val message = if (itemCount > 1) {
            "$randomMessage You have $itemCount items waiting for checkout."
        } else {
            "$randomMessage You have $itemCount item waiting for checkout."
        }
        
        // Use NotificationBannerManager with Shopee styling
        NotificationBannerManager.showCartReminderBanner(
            context = applicationContext,
            title = title,
            message = message,
            itemCount = itemCount,
            iconResId = R.drawable.ic_cart_notification,
            intent = intent
        )
    }
} 