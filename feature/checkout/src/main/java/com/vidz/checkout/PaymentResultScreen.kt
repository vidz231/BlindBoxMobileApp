package com.vidz.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vidz.base.components.PrimaryButton

@Composable
fun PaymentResultScreen(
    paymentResult: PaymentResult,
    orderId: String? = null,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onRetryPayment: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    //region Define Var
    val scrollState = rememberScrollState()
    //endregion

    //region ui
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (paymentResult) {
            is PaymentResult.Success -> {
                PaymentSuccessContent(
                    orderId = orderId,
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToOrders = onNavigateToOrders
                )
            }
            is PaymentResult.Failed -> {
                PaymentFailedContent(
                    errorMessage = paymentResult.errorMessage,
                    onNavigateToHome = onNavigateToHome,
                    onRetryPayment = onRetryPayment
                )
            }
            is PaymentResult.Cancelled -> {
                PaymentCancelledContent(
                    onNavigateToHome = onNavigateToHome,
                    onRetryPayment = onRetryPayment
                )
            }
        }
    }
    //endregion
}

@Composable
private fun PaymentSuccessContent(
    orderId: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    PaymentResultContent(
        icon = Icons.Default.CheckCircle,
        iconColor = MaterialTheme.colorScheme.primary,
        title = "Payment Successful!",
        subtitle = "Your order has been placed successfully",
        description = if (orderId != null) {
            "Order ID: #$orderId\n\nYou will receive an email confirmation shortly. You can track your order in the Orders section."
        } else {
            "You will receive an email confirmation shortly. You can track your order in the Orders section."
        },
        primaryButtonText = "View Orders",
        onPrimaryButtonClick = onNavigateToOrders,
        secondaryButtonText = "Continue Shopping",
        onSecondaryButtonClick = onNavigateToHome
    )
}

@Composable
private fun PaymentFailedContent(
    errorMessage: String,
    onNavigateToHome: () -> Unit,
    onRetryPayment: (() -> Unit)?
) {
    PaymentResultContent(
        icon = Icons.Default.Error,
        iconColor = MaterialTheme.colorScheme.error,
        title = "Payment Failed",
        subtitle = "Your payment could not be processed",
        description = errorMessage,
        primaryButtonText = if (onRetryPayment != null) "Retry Payment" else "Back to Home",
        onPrimaryButtonClick = onRetryPayment ?: onNavigateToHome,
        secondaryButtonText = if (onRetryPayment != null) "Back to Home" else null,
        onSecondaryButtonClick = if (onRetryPayment != null) onNavigateToHome else null
    )
}

@Composable
private fun PaymentCancelledContent(
    onNavigateToHome: () -> Unit,
    onRetryPayment: (() -> Unit)?
) {
    PaymentResultContent(
        icon = Icons.Default.Cancel,
        iconColor = MaterialTheme.colorScheme.outline,
        title = "Payment Cancelled",
        subtitle = "You cancelled the payment process",
        description = "Your order has not been placed. You can try again or continue shopping.",
        primaryButtonText = if (onRetryPayment != null) "Try Again" else "Continue Shopping",
        onPrimaryButtonClick = onRetryPayment ?: onNavigateToHome,
        secondaryButtonText = if (onRetryPayment != null) "Continue Shopping" else null,
        onSecondaryButtonClick = if (onRetryPayment != null) onNavigateToHome else null
    )
}

@Composable
private fun PaymentResultContent(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    description: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryButtonClick: (() -> Unit)? = null
) {
    // Icon
    Icon(
        imageVector = icon,
        contentDescription = title,
        tint = iconColor,
        modifier = Modifier.size(80.dp)
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Title
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Subtitle
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Description
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Primary Button
    PrimaryButton(
        text = primaryButtonText,
        onClick = onPrimaryButtonClick,
        modifier = Modifier.fillMaxWidth()
    )
    
    // Secondary Button (if provided)
    if (secondaryButtonText != null && onSecondaryButtonClick != null) {
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onSecondaryButtonClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(secondaryButtonText)
        }
    }
} 