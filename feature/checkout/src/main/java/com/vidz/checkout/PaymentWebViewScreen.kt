package com.vidz.checkout

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoading = false
                    url?.let { handleUrl(it) }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    handleUrl(url)
                    return false // Let WebView load it
                }

                fun handleUrl(u: String) {
                    when {
                        u.contains("vnp_ResponseCode=00") -> onPaymentResult(PaymentResult.Success)
                        u.contains("vnp_ResponseCode=") && !u.contains("vnp_ResponseCode=00") -> {
                            val code = extractResponseCode(u)
                            onPaymentResult(PaymentResult.Failed(getVNPayErrorMessage(code)))
                        }
                        u.contains("/Payment/Error.html?code=") -> {
                            val code = extractErrorCode(u)
                            onPaymentResult(PaymentResult.Failed("VNPay error code: $code"))
                        }
                        u.contains("cancel") || u.contains("error") -> onPaymentResult(PaymentResult.Cancelled)
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
        webView.loadUrl(paymentUrl)
    }

    //region ui
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBarWithBack(title = "Payment", onBackClick = onBackClick)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator()
                    Text("Loading payment page...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        AndroidView(factory = { webView }, update = { /* no-op, handled by loadUrl effect */ }, modifier = Modifier.fillMaxSize())
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