package com.vidz.checkout

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.view.ViewGroup
import android.webkit.CookieManager
import com.vidz.base.components.TopAppBarWithBack
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentWebViewScreen(
    paymentUrl: String,
    onBackClick: () -> Unit,
    onPaymentResult: (PaymentResult) -> Unit,
    modifier: Modifier = Modifier
) {
    //region Define Var
    var isLoading by remember { mutableStateOf(true) }
    var hasHandledResult by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Create & remember WebView so it survives recomposition
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccess = false
                allowContentAccess = false
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isLoading = true
                    android.util.Log.d("PaymentWebView", "Page started loading: $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoading = false
                    android.util.Log.d("PaymentWebView", "Page finished loading: $url")
                    
                    // Only handle URL on page finish if it's a result URL (success/failure/cancel)
                    // Don't immediately handle error pages to let user see them
                    url?.let { 
                        if (shouldHandleUrlImmediately(it) && !hasHandledResult) {
                            hasHandledResult = true
                            handleUrl(it)
                        }
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    android.util.Log.d("PaymentWebView", "URL loading: $url")
                    
                    // Only handle result URLs, let other URLs load normally
                    if (shouldHandleUrlImmediately(url) && !hasHandledResult) {
                        hasHandledResult = true
                        handleUrl(url)
                        return true // Prevent further loading since we're handling the result
                    }
                    
                    return false // Let WebView load it
                }

                private fun shouldHandleUrlImmediately(url: String): Boolean {
                    return when {
                        // Only handle definitive success responses
                        url.contains("vnp_ResponseCode=00") -> true
                        // Handle specific failure response codes (not error pages)
                        url.contains("vnp_ResponseCode=") && !url.contains("vnp_ResponseCode=00") && !url.contains("/Payment/Error.html") -> true
                        // Handle explicit cancel URLs
                        url.contains("cancel") && !url.contains("/Payment/Error.html") -> true
                        else -> false
                    }
                }

                private fun handleUrl(u: String) {
                    android.util.Log.d("PaymentWebView", "Handling URL result: $u")
                    when {
                        u.contains("vnp_ResponseCode=00") -> {
                            android.util.Log.d("PaymentWebView", "Payment successful")
                            onPaymentResult(PaymentResult.Success)
                        }
                        u.contains("vnp_ResponseCode=") && !u.contains("vnp_ResponseCode=00") -> {
                            val code = extractResponseCode(u)
                            android.util.Log.d("PaymentWebView", "Payment failed with response code: $code")
                            onPaymentResult(PaymentResult.Failed(getVNPayErrorMessage(code)))
                        }
                        u.contains("cancel") -> {
                            android.util.Log.d("PaymentWebView", "Payment cancelled")
                            onPaymentResult(PaymentResult.Cancelled)
                        }
                    }
                }
            }
        }.also { webView ->
            // Configure cookies after WebView creation
            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
            }
        }
    }
    //endregion

    // Load URL on first composition or when url changes
    LaunchedEffect(paymentUrl) {
        android.util.Log.d("PaymentWebView", "Loading payment URL: $paymentUrl")
        hasHandledResult = false // Reset for new payment URL
        webView.loadUrl(paymentUrl)
    }

    //region ui
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBarWithBack(
            title = "Payment", 
            onBackClick = {
                android.util.Log.d("PaymentWebView", "Back clicked from payment webview")
                onBackClick()
            }
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator()
                    Text("Loading payment page...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { webView }, 
                update = { /* no-op, handled by loadUrl effect */ }, 
                modifier = Modifier.fillMaxSize()
            )
            
            // Add a floating action button to manually trigger error handling if needed
            // This helps when the user sees an error page and wants to go back
            if (!isLoading && !hasHandledResult) {
                FloatingActionButton(
                    onClick = {
                        android.util.Log.d("PaymentWebView", "Manual error handling triggered")
                        val currentUrl = webView.url
                        if (currentUrl != null && currentUrl.contains("/Payment/Error.html?code=")) {
                            hasHandledResult = true
                            val code = extractErrorCode(currentUrl)
                            onPaymentResult(PaymentResult.Failed("VNPay error code: $code"))
                        } else {
                            // If it's not an error page, assume user wants to cancel
                            hasHandledResult = true
                            onPaymentResult(PaymentResult.Cancelled)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Payment",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
    //endregion
}

sealed class PaymentResult {
    data object Success : PaymentResult()
    data class Failed(val errorMessage: String) : PaymentResult()
    data object Cancelled : PaymentResult()
}

private fun extractResponseCode(url: String): String {
    return try {
        val regex = Regex("vnp_ResponseCode=([^&]+)")
        regex.find(url)?.groupValues?.get(1) ?: "99"
    } catch (e: Exception) {
        "99"
    }
}

private fun getVNPayErrorMessage(responseCode: String): String {
    return when (responseCode) {
        "01" -> "Transaction is incomplete"
        "02" -> "Transaction failed"
        "04" -> "Transaction was reversed"
        "05" -> "Transaction processing by VNPAY"
        "06" -> "Transaction was reversed"
        "07" -> "Transaction was suspected as fraud"
        "09" -> "Customer cancelled the transaction"
        "10" -> "Customer authentication failed"
        "11" -> "Payment deadline expired"
        "12" -> "Customer's card/account was locked"
        "13" -> "Customer entered wrong OTP"
        "24" -> "Customer cancelled the transaction"
        "51" -> "Insufficient account balance"
        "65" -> "Customer exceeded daily transaction limit"
        "75" -> "Payment bank is under maintenance"
        "79" -> "Customer entered payment password incorrectly too many times"
        "99" -> "Other error"
        else -> "Payment failed with code: $responseCode"
    }
}

private fun extractErrorCode(url: String): String {
    val regex = Regex("code=([^&]+)")
    return regex.find(url)?.groupValues?.get(1) ?: "99"
} 