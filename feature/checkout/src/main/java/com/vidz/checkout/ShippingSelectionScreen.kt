package com.vidz.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vidz.base.components.PrimaryButton
import com.vidz.base.components.TopAppBarWithBack
import com.vidz.domain.model.ShippingInfo

@Composable
fun ShippingSelectionScreen(
    onBackClick: () -> Unit,
    onShippingInfoSelected: (ShippingInfo) -> Unit,
    onCreateNewShippingInfo: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShippingSelectionViewModel = hiltViewModel()
) {
    //region Define Var
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedShippingInfo by remember { mutableStateOf<ShippingInfo?>(null) }
    //endregion

    //region Event Handler
    LaunchedEffect(Unit) {
        viewModel.onTriggerEvent(ShippingSelectionViewModel.ShippingSelectionViewEvent.LoadShippingInfos)
    }

    val handleRefresh = {
        viewModel.onTriggerEvent(ShippingSelectionViewModel.ShippingSelectionViewEvent.RefreshShippingInfos)
    }

    val handleShippingInfoSelect = { shippingInfo: ShippingInfo ->
        selectedShippingInfo = shippingInfo
    }

    val handleConfirmSelection: () -> Unit = {
        selectedShippingInfo?.let { shippingInfo ->
            onShippingInfoSelected(shippingInfo)
            onBackClick()
        } ?: Unit
    }

    val handleCreateNew = {
        onCreateNewShippingInfo()
    }
    //endregion

    //region ui
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBarWithBack(
            title = "Select Shipping Address",
            onBackClick = onBackClick
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                    // Mapbox Integration Area
                    Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Map",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Mapbox Integration Area",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Map will be rendered here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                // Refresh Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = handleRefresh,
                            enabled = !uiState.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Refresh")
                        }
                    }
                }
                
                // Create New Address Button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = handleCreateNew,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add New",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Add New Shipping Address",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Existing Shipping Addresses
                items(uiState.shippingInfos) { shippingInfo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { handleShippingInfoSelect(shippingInfo) },
                        colors = if (selectedShippingInfo?.shippingInfoId == shippingInfo.shippingInfoId) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = shippingInfo.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = shippingInfo.phoneNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${shippingInfo.address}, ${shippingInfo.ward}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${shippingInfo.district}, ${shippingInfo.city}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            if (selectedShippingInfo?.shippingInfoId == shippingInfo.shippingInfoId) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                if (uiState.shippingInfos.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "No Address",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No shipping addresses found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add a new address to continue",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Confirm Selection Button
                if (selectedShippingInfo != null) {
                    item {
                        PrimaryButton(
                            text = "Use This Address",
                            onClick = handleConfirmSelection,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                if (uiState.errorMessage.isNotEmpty()) {
                    item {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                }
            }
        }
    }

    //region Dialog and Sheet
    //end region
    //end region
} 