package com.vidz.detail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewState
import com.kevinnzou.web.rememberWebViewStateWithHTMLData
import com.vidz.domain.model.BlindBox
import com.vidz.domain.model.StockKeepingUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    navController: NavController,
    blindBoxId: Long,
    initialImageUrl: String,
    initialTitle: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onShowSnackbar: (String) -> Unit,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    //region Define Var
    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    
    // State management
    var selectedSkuIndex by rememberSaveable { mutableIntStateOf(0) }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    
    // Derived state to prevent unnecessary recompositions
    val currentBlindBox by remember(uiState.blindBox) { 
        derivedStateOf { uiState.blindBox }
    }
    
    val displayData by remember(currentBlindBox, initialImageUrl, initialTitle) {
        derivedStateOf {
            DetailDisplayData(
                title = currentBlindBox?.name ?: initialTitle,
                imageUrls = currentBlindBox?.images?.mapNotNull { it.imageUrl.takeIf { url -> url.isNotBlank() } }
                    ?: listOfNotNull(initialImageUrl.takeIf { it.isNotBlank() }),
                brandName = currentBlindBox?.brand?.name.orEmpty(),
                description = currentBlindBox?.description.orEmpty(),
                skus = currentBlindBox?.skus.orEmpty()
            )
        }
    }
    
    val selectedSku by remember(displayData.skus, selectedSkuIndex) {
        derivedStateOf { displayData.skus.getOrNull(selectedSkuIndex) }
    }
    
    // Handle add to cart success/error states
    LaunchedEffect(uiState.addToCartSuccess, uiState.addToCartError) {
        when {
            uiState.addToCartSuccess -> {
                selectedSku?.let { sku ->
                    onShowSnackbar("✓ ${sku.name} added to cart!")
                }
                detailViewModel.clearAddToCartState()
            }
            uiState.addToCartError != null -> {
                onShowSnackbar("❌ ${uiState.addToCartError}")
                detailViewModel.clearAddToCartState()
            }
        }
    }
    
    // Load data only once
    LaunchedEffect(blindBoxId) {
        if (blindBoxId > 0 && currentBlindBox == null) {
            detailViewModel.loadBlindBoxDetails(blindBoxId)
        }
    }
    //endregion

    //region Event Handler
    val onBack = { navController.navigateUp() }
    val onShare = { onShowSnackbar("Share functionality coming soon!") }
    val onFavoriteToggle = { isFavorite = !isFavorite }
    val onSkuSelect = { index: Int -> selectedSkuIndex = index }
    val onAddToCart = { 
        selectedSku?.let { sku ->
            detailViewModel.onTriggerEvent(
                DetailViewModel.DetailViewEvent.AddToCart(
                    sku = sku,
                    quantity = 1,
                    slot = null // For now, no slot selection. Can be extended later
                )
            )
        }
    }
    val onBuyNow = {
        selectedSku?.let { sku ->
            onShowSnackbar("Proceeding to checkout with ${sku.name}")
        }
    }
    //endregion

    //region UI
    Scaffold(
        topBar = {
            DetailTopBar(
                title = displayData.title,
                onBack = { onBack.invoke() },
                onShare = onShare,
                onFavoriteToggle = onFavoriteToggle,
                isFavorite = isFavorite
            )
        },
        bottomBar = {
            if (displayData.skus.isNotEmpty() && selectedSku != null) {
                DetailBottomBar(
                    selectedSku = selectedSku!!,
                    isAddingToCart = uiState.isAddingToCart,
                    onAddToCart = { onAddToCart.invoke() },
                    onBuyNow = { onBuyNow.invoke() }
                )
            }
        }
    ) { paddingValues ->
        DetailContent(
            modifier = Modifier.padding(paddingValues),
            displayData = displayData,
            selectedSkuIndex = selectedSkuIndex,
            isLoading = uiState.isLoading && uiState.error == null,
            error = uiState.error,
            blindBoxId = blindBoxId,
            onSkuSelect = onSkuSelect,
            onRetry = { detailViewModel.loadBlindBoxDetails(blindBoxId) }
        )
    }
    //endregion
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    title: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isFavorite: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = title.takeIf { it.isNotBlank() } ?: "Detail",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun DetailContent(
    modifier: Modifier = Modifier,
    displayData: DetailDisplayData,
    selectedSkuIndex: Int,
    isLoading: Boolean,
    error: String?,
    blindBoxId: Long,
    onSkuSelect: (Int) -> Unit,
    onRetry: () -> Unit
) {
    when {
        isLoading -> {
            // Show loading state as default
            DetailLoadingState(
                modifier = modifier.fillMaxSize()
            )
        }
        error != null -> {
            // Show error only if there's an actual error and not loading
            DetailErrorState(
                error = error,
                onRetry = onRetry,
                modifier = modifier.fillMaxSize()
            )
        }
        else -> {
            // Show content (with initial data or loaded data)
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Image Section
                DetailImageSection(
                    imageUrls = displayData.imageUrls,
                    title = displayData.title
                )
                
                // Content Section
                DetailInfoSection(
                    displayData = displayData,
                    selectedSkuIndex = selectedSkuIndex,
                    isLoading = false, // Don't show loading in info section when we have initial data
                    onSkuSelect = onSkuSelect
                )
                
                // Bottom padding for bottom bar
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailImageSection(
    imageUrls: List<String>,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        if (imageUrls.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { imageUrls.size })
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { page -> imageUrls[page] } // Stable key to prevent flickering
            ) { page ->
                DetailImage(
                    imageUrl = imageUrls[page],
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Page Indicators
            if (imageUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(imageUrls.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    }
                                )
                        )
                    }
                }
            }
        } else {
            // Placeholder when no images
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "No image",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No Image Available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        error = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "Failed to load image",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Failed to load image",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
private fun DetailInfoSection(
    displayData: DetailDisplayData,
    selectedSkuIndex: Int,
    isLoading: Boolean,
    onSkuSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title and Brand
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = displayData.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (displayData.brandName.isNotBlank()) {
                Text(
                    text = displayData.brandName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // SKU Selection
        if (displayData.skus.isNotEmpty()) {
            CleanSkuSelection(
                skus = displayData.skus,
                selectedIndex = selectedSkuIndex,
                onSkuSelect = onSkuSelect
            )
        }
        
        // Description with HTML rendering
        if (displayData.description.isNotBlank()) {
            CleanDescriptionSection(description = displayData.description)
        }
    }
}

@Composable
private fun CleanSkuSelection(
    skus: List<StockKeepingUnit>,
    selectedIndex: Int,
    onSkuSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Choose Option",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(skus, key = { _, sku -> sku.skuId }) { index, sku ->
                CleanSkuCard(
                    sku = sku,
                    isSelected = index == selectedIndex,
                    onClick = { onSkuSelect(index) }
                )
            }
        }
    }
}

@Composable
private fun CleanSkuCard(
    sku: StockKeepingUnit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .width(120.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null,
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = sku.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "$${sku.price}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = if (sku.stock > 0) "${sku.stock} left" else "Out of stock",
                style = MaterialTheme.typography.bodySmall,
                color = if (sku.stock > 0) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
private fun CleanDescriptionSection(description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 1.dp
        ) {
            // HTML WebView for rich content
            val webViewState = rememberWebViewStateWithHTMLData(
                data = """
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            body {
                                margin: 0;
                                padding: 20px;
                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                                font-size: 16px;
                                line-height: 1.6;
                                color: #333333;
                                background-color: transparent;
                                word-wrap: break-word;
                                overflow-wrap: break-word;
                            }
                            * {
                                max-width: 100% !important;
                                box-sizing: border-box;
                            }
                            img {
                                height: auto !important;
                                width: auto !important;
                                max-width: 100% !important;
                                border-radius: 8px;
                                margin: 12px 0;
                                display: block;
                            }
                            p {
                                margin: 0 0 16px 0;
                                text-align: justify;
                            }
                            h1, h2, h3, h4, h5, h6 {
                                margin: 24px 0 16px 0;
                                font-weight: 600;
                                line-height: 1.3;
                                color: #1a1a1a;
                            }
                            h1 { font-size: 24px; }
                            h2 { font-size: 20px; }
                            h3 { font-size: 18px; }
                            h4 { font-size: 16px; }
                            h5 { font-size: 14px; }
                            h6 { font-size: 12px; }
                            ul, ol {
                                margin: 16px 0;
                                padding-left: 24px;
                            }
                            li {
                                margin-bottom: 8px;
                                line-height: 1.5;
                            }
                            a {
                                color: #6200EE;
                                text-decoration: none;
                                word-break: break-all;
                            }
                            a:hover {
                                text-decoration: underline;
                            }
                            blockquote {
                                border-left: 4px solid #6200EE;
                                margin: 20px 0;
                                padding: 16px 0 16px 20px;
                                font-style: italic;
                                background-color: rgba(98, 0, 238, 0.05);
                                border-radius: 0 8px 8px 0;
                            }
                            table {
                                width: 100%;
                                border-collapse: collapse;
                                margin: 16px 0;
                            }
                            th, td {
                                border: 1px solid #ddd;
                                padding: 12px;
                                text-align: left;
                            }
                            th {
                                background-color: #f5f5f5;
                                font-weight: 600;
                            }
                            code {
                                background-color: #f5f5f5;
                                padding: 2px 6px;
                                border-radius: 4px;
                                font-family: 'Monaco', 'Consolas', monospace;
                                font-size: 14px;
                            }
                            pre {
                                background-color: #f5f5f5;
                                padding: 16px;
                                border-radius: 8px;
                                overflow-x: auto;
                                margin: 16px 0;
                            }
                            strong, b {
                                font-weight: 600;
                            }
                            em, i {
                                font-style: italic;
                            }
                            hr {
                                border: none;
                                height: 2px;
                                background-color: #e0e0e0;
                                margin: 24px 0;
                            }
                        </style>
                    </head>
                    <body>
                        $description
                    </body>
                    </html>
                """.trimIndent()
            )
            
            WebView(
                state = webViewState,
                modifier = Modifier
                    .fillMaxWidth(),
                onCreated = { webView ->
                    webView.settings.apply {
                        javaScriptEnabled = false
                        domStorageEnabled = false
                        allowFileAccess = false
                        allowContentAccess = false
                        setSupportZoom(false)
                        builtInZoomControls = false
                        displayZoomControls = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailBottomBar(
    selectedSku: StockKeepingUnit,
    isAddingToCart: Boolean,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Price and Stock Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$${selectedSku.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${selectedSku.stock} available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action Buttons
            OutlinedButton(
                onClick = onAddToCart,
                modifier = Modifier.weight(1f),
                enabled = selectedSku.stock > 0 && !isAddingToCart
            ) {
                if (isAddingToCart) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Adding...")
                    }
                } else {
                    Text("Add to Cart")
                }
            }
            
            Button(
                onClick = onBuyNow,
                modifier = Modifier.weight(1f),
                enabled = selectedSku.stock > 0 && !isAddingToCart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Buy Now")
            }
        }
    }
}

@Composable
private fun DetailLoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Loading product details...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DetailErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

// Data class for clean state management
private data class DetailDisplayData(
    val title: String,
    val imageUrls: List<String>,
    val brandName: String,
    val description: String,
    val skus: List<StockKeepingUnit>
)