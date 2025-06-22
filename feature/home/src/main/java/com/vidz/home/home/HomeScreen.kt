package com.vidz.home.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vidz.base.components.GeneralAppBar
import com.vidz.base.components.LoadMoreButton
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.domain.model.BlindBox
import java.net.URLEncoder


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenRoot(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState = homeViewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        navController = navController,
        homeUiState = homeUiState,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onEvent = homeViewModel::onTriggerEvent
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeUiState: State<HomeViewModel.HomeViewState>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onEvent: (HomeViewModel.HomeViewEvent) -> Unit
) {
    //region Define Var
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    //endregion

    //region Event Handler
    val onItemClick: (BlindBox) -> Unit = { blindBox ->
        // Navigate using blindBoxId, imageUrl, and title for immediate display
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
            GeneralAppBar(
                leadingContent = {
                    Text(
                        "BlindBox Collection",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onCartClick = onCartClick,
                onChatClick = onChatClick,
                isLargeAppBar = false
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val state = homeUiState.value

        when {
            state.isLoading && state.blindBoxes.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null && state.blindBoxes.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (state.blindBoxes.isEmpty() && !state.isLoading) {
                        // Empty state
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "No blind boxes available",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Check back later for new items",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    itemsIndexed(state.blindBoxes) { index, blindBox ->
                        BlindBoxItem(
                            blindBox = blindBox,
                            onClick = { onItemClick(blindBox) },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope
                        )
                        
                        // Trigger automatic load more when approaching the end (last 4 items)
                        LaunchedEffect(index) {
                            if (index >= state.blindBoxes.size - 4 && state.hasMoreData && !state.isLoadingMore) {
                                onEvent(HomeViewModel.HomeViewEvent.LoadMore)
                            }
                        }
                    }
                    
                    // Load more button/indicator that spans both columns
                    if (state.hasMoreData || state.isLoadingMore) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            LoadMoreButton(
                                onClick = { onEvent(HomeViewModel.HomeViewEvent.LoadMore) },
                                isLoading = state.isLoadingMore,
                                hasMoreData = state.hasMoreData
                            )
                        }
                    } else if (state.blindBoxes.isNotEmpty()) {
                        // End of list indicator
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "You've reached the end!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //region Dialog and Sheet
    //endregion
    //endregion
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BlindBoxItem(
    blindBox: BlindBox,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                // Image with shared element transition
                AsyncImage(
                    model = blindBox.images.firstOrNull()?.imageUrl ?: "",
                    contentDescription = blindBox.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "image-${blindBox.blindBoxId}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                )
                
                // Title with shared element transition
                Text(
                    text = blindBox.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "title-${blindBox.blindBoxId}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .padding(12.dp)
                )
            }
        }
    }
}
