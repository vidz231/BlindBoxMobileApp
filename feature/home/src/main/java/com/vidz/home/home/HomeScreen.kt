package com.vidz.home.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import com.vidz.base.components.GeneralAppBar
import com.vidz.base.components.LoadMoreButton
import com.vidz.base.components.SkeletonBlindBoxGrid
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.domain.model.BlindBox
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.vidz.blindbox.feature.home.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState = homeViewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        navController = navController,
        homeUiState = homeUiState,
        onEvent = homeViewModel::onTriggerEvent
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeUiState: State<HomeViewModel.HomeViewState>,
    onEvent: (HomeViewModel.HomeViewEvent) -> Unit
) {
    //region Define Var
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    //endregion

    //region Event Handler
    val onItemClick: (BlindBox) -> Unit = { blindBox ->
        val imageUrl = blindBox.images.firstOrNull()?.imageUrl ?: ""
        val encodedImageUrl = java.net.URLEncoder.encode(imageUrl, "UTF-8")
        val encodedTitle = java.net.URLEncoder.encode(blindBox.name, "UTF-8")
        navController.navigate("${DestinationRoutes.ITEM_DETAIL_SCREEN_BASE_ROUTE}/${blindBox.blindBoxId}/$encodedImageUrl/$encodedTitle")
    }

    val onCartClick: () -> Unit = {
        navController.navigate(DestinationRoutes.CART_SCREEN_ROUTE)
    }

    val onChatClick: () -> Unit = {
        navController.navigate(DestinationRoutes.MESSAGE_SCREEN_ROUTE)
    }
    //endregion

    //region ui
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ModernAppBar(
                scrollBehavior = scrollBehavior,
                onCartClick = onCartClick,
                cartItemsCount = homeUiState.value.cartItemsCount,
                onChatClick = onChatClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val state = homeUiState.value

        Log.d("HomeScreen", "Recomposing - isLoading: ${state.isLoading}, isRefreshing: ${state.isRefreshing}, blindBoxes: ${state.blindBoxes.size}, error: ${state.error}")

        val pullRefreshState = rememberPullToRefreshState()
        val isRefreshing = state.isRefreshing

        Log.d("HomeScreen", "PullToRefresh state - isRefreshing: $isRefreshing")

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = pullRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                Log.d("HomeScreen", "Pull to refresh triggered!")
                onEvent(HomeViewModel.HomeViewEvent.Refresh)
            },
        ) {
            when {
                (state.isLoading && state.blindBoxes.isEmpty()) || state.isRefreshing -> {
                    ModernSkeletonGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        itemCount = 6
                    )
                }
                state.error != null && state.blindBoxes.isEmpty() -> {
                    ModernErrorState(
                        error = state.error,
                        onRetry = { onEvent(HomeViewModel.HomeViewEvent.Refresh) }
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        if (state.blindBoxes.isEmpty() && !state.isLoading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                ModernEmptyState()
                            }
                        }

                        itemsIndexed(state.blindBoxes) { index, blindBox ->
                            ModernBlindBoxItem(
                                blindBox = blindBox,
                                onClick = { onItemClick(blindBox) },
                                index = index
                            )

                            LaunchedEffect(index) {
                                if (index >= state.blindBoxes.size - 4 && state.hasMoreData && !state.isLoadingMore) {
                                    onEvent(HomeViewModel.HomeViewEvent.LoadMore)
                                }
                            }
                        }

                        if (state.hasMoreData || state.isLoadingMore) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                ModernLoadMoreButton(
                                    onClick = { onEvent(HomeViewModel.HomeViewEvent.LoadMore) },
                                    isLoading = state.isLoadingMore,
                                    hasMoreData = state.hasMoreData
                                )
                            }
                        } else if (state.blindBoxes.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                ModernEndIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
    //endregion
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAppBar(
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    onCartClick: () -> Unit,
    cartItemsCount: Int,
    onChatClick: () -> Unit
) {
    GeneralAppBar(
        leadingContent = {
            Column {
                Text(
                    "Discovery",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Explore premium blind boxes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        scrollBehavior = scrollBehavior,
        onCartClick = onCartClick,
        cartItemsCount = cartItemsCount,
        onChatClick = onChatClick,
        isLargeAppBar = false
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ModernBlindBoxItem(
    blindBox: BlindBox,
    onClick: () -> Unit,
    index: Int
) {
        var isPressed by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = tween(durationMillis = 150),
            label = "scale"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClick()
                }
                .graphicsLayer {
                    // Add subtle shadow and elevation
                    shadowElevation = 8.dp.toPx()
                    clip = true
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp,
                pressedElevation = 12.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Image container with modern styling
                var imageLoadFailed by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!imageLoadFailed && !blindBox.images.firstOrNull()?.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = blindBox.images.firstOrNull()?.imageUrl ?: "",
                            contentDescription = blindBox.name,
                            contentScale = ContentScale.Crop,
                            onState = { state ->
                                imageLoadFailed = state is AsyncImagePainter.State.Error
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )

                        // Subtle overlay for better text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.1f)
                                        ),
                                        startY = 0.6f
                                    )
                                )
                        )
                    }

                    // Modern placeholder
                    if (imageLoadFailed || blindBox.images.firstOrNull()?.imageUrl.isNullOrEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.feature_home_ic_image_placeholder),
                                contentDescription = "Image placeholder",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Preview",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Title and info section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = blindBox.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()

                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Subtitle or additional info
                    Text(
                        text = "Collectible • Limited Edition",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
    }
}

@Composable
fun ModernSkeletonGrid(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        repeat(itemCount) {
            item {
                ModernSkeletonItem()
            }
        }
    }
}

@Composable
fun ModernSkeletonItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Skeleton image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .clip(RoundedCornerShape(20.dp))
            )

            // Skeleton text
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(12.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun ModernEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.feature_home_ic_image_placeholder),
                contentDescription = "Empty state",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )

            Text(
                text = "Nothing here yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = "New blind boxes will appear here\nwhen they become available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ModernErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.feature_home_ic_image_placeholder),
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )

            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Modern retry button would go here
            Text(
                text = "Tap to retry",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}

@Composable
fun ModernLoadMoreButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    hasMoreData: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        LoadMoreButton(
            onClick = onClick,
            isLoading = isLoading,
            hasMoreData = hasMoreData
        )
    }
}

@Composable
fun ModernEndIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(1.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
            )

            Text(
                text = "All caught up",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(1.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
            )
        }
    }
}