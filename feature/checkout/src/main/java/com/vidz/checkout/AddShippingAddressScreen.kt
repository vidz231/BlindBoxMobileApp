package com.vidz.checkout

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.geojson.Point
import com.vidz.base.components.MapComposePicker
import com.vidz.base.components.reverseGeocode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.vidz.checkout.AddShippingAddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShippingAddressScreen(
    onBackClick: () -> Unit,
    onShippingAddressAdded: () -> Unit,
    viewModel: AddShippingAddressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showMapPicker by remember { mutableStateOf(false) }
    /**
     * Controls whether the auto-complete dropdown is visible. Instead of tying the visibility
     * directly to focus changes (which can unintentionally close the software keyboard), we keep
     * the dropdown open as long as it has items and until the user explicitly chooses one or
     * taps outside to dismiss. */
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Shipping Address") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //region Map Picker
            if (showMapPicker) {
                Box(Modifier.height(220.dp)) {
                    MapComposePicker(
                        modifier = Modifier.fillMaxSize(),
                        onPointSelected = { point ->
                            viewModel.onMapPointSelected(point, context)
                        },
                        onUserLocationUpdated = { }
                    )
                }

                // Add a button to close the map picker manually
                Button(
                    onClick = { showMapPicker = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Map")
                }
            } else {
                Button(onClick = { showMapPicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Choose from Map or Use My Current Location")
                }
            }
            //endregion

            //region Address Search with Goong API Auto-complete
            // Keep the dropdown anchored to the text field so users can continue typing while
            // reading suggestions. The menu remains visible as long as there are suggestions
            // and until the user explicitly dismisses it or selects an item.
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { query ->
                    viewModel.onTriggerEvent(
                        AddShippingAddressViewModel.AddShippingAddressViewEvent.SearchAddress(query)
                    )
                    expanded = true
                },
                label = { Text("Search Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        hasFocus = it.isFocused
                    },
                trailingIcon = {
                    if (uiState.isLoadingSuggestions) {
                        CircularProgressIndicator(modifier = Modifier.height(16.dp))
                    }
                },
                singleLine = true
            )

            DropdownMenu(
                expanded = expanded && uiState.autoCompleteSuggestions.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                uiState.autoCompleteSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = suggestion.description,
                                maxLines = 2
                            )
                        },
                        onClick = {
                            viewModel.onTriggerEvent(
                                AddShippingAddressViewModel.AddShippingAddressViewEvent.SelectAutoCompleteSuggestion(suggestion)
                            )
                            expanded = false
                            focusRequester.requestFocus()
                        }
                    )
                }
            }
            //endregion

            //region City/Ward/District Suggestions
            OutlinedTextField(
                value = uiState.city,
                onValueChange = { viewModel.onTriggerEvent(AddShippingAddressViewModel.AddShippingAddressViewEvent.UpdateCity(it)) },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.district,
                onValueChange = { viewModel.onTriggerEvent(AddShippingAddressViewModel.AddShippingAddressViewEvent.UpdateDistrict(it)) },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.ward,
                onValueChange = { viewModel.onTriggerEvent(AddShippingAddressViewModel.AddShippingAddressViewEvent.UpdateWard(it)) },
                label = { Text("Ward") },
                modifier = Modifier.fillMaxWidth()
            )
            //endregion

            //region Form Fields
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onTriggerEvent(AddShippingAddressViewModel.AddShippingAddressViewEvent.UpdateName(it)) },
                label = { Text("Recipient Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = { viewModel.onTriggerEvent(AddShippingAddressViewModel.AddShippingAddressViewEvent.UpdatePhone(it)) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.address,
                onValueChange = { viewModel.onTriggerEvent(AddShippingAddressViewModel.AddShippingAddressViewEvent.UpdateAddress(it)) },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            //endregion

            //region Save Button
            Button(
                onClick = {
                    // Only save when user explicitly clicks this button
                    viewModel.onTriggerEvent(AddShippingAddressViewModel.AddShippingAddressViewEvent.Save)
                },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Address")
            }
            if (uiState.errorMessage != null) {
                Text("Error has occurred", color = MaterialTheme.colorScheme.error)
            }
            if (uiState.success) {
                LaunchedEffect(Unit) { onShippingAddressAdded() }
            }
            //endregion
        }
    }
}