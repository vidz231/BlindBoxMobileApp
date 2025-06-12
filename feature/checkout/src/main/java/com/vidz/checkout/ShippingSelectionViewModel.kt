package com.vidz.checkout

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Success
import com.vidz.domain.model.ShippingInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShippingSelectionViewModel @Inject constructor(
    // TODO: Inject ShippingInfoRepository when implemented
) : BaseViewModel<
        ShippingSelectionViewModel.ShippingSelectionViewEvent,
        ShippingSelectionViewModel.ShippingSelectionViewState,
        ShippingSelectionViewModel.ShippingSelectionViewModelState
        >(ShippingSelectionViewModelState()) {

    sealed class ShippingSelectionViewEvent : ViewEvent {
        data object LoadShippingInfos : ShippingSelectionViewEvent()
        data class DeleteShippingInfo(val shippingInfoId: Long) : ShippingSelectionViewEvent()
    }

    data class ShippingSelectionViewState(
        val isLoading: Boolean = true,
        val shippingInfos: List<ShippingInfo> = emptyList(),
        val errorMessage: String = ""
    ) : ViewState()

    data class ShippingSelectionViewModelState(
        val isLoading: Boolean = true,
        val shippingInfos: List<ShippingInfo> = emptyList(),
        val errorMessage: String = ""
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return ShippingSelectionViewState(
                isLoading = isLoading,
                shippingInfos = shippingInfos,
                errorMessage = errorMessage
            )
        }
    }

    override fun onTriggerEvent(event: ShippingSelectionViewEvent) {
        when (event) {
            is ShippingSelectionViewEvent.LoadShippingInfos -> loadShippingInfos()
            is ShippingSelectionViewEvent.DeleteShippingInfo -> deleteShippingInfo(event.shippingInfoId)
        }
    }

    private fun loadShippingInfos() {
        viewModelScope.launch {
            try {
                viewModelState.update { it.copy(isLoading = true) }
                
                // TODO: Replace with actual repository call
                // For now, create mock data
                val mockShippingInfos = listOf(
                    ShippingInfo(
                        shippingInfoId = 1L,
                        name = "John Doe",
                        phoneNumber = "0123456789",
                        address = "123 Main Street",
                        ward = "Ward 1",
                        district = "District 1", 
                        city = "Ho Chi Minh City"
                    ),
                    ShippingInfo(
                        shippingInfoId = 2L,
                        name = "Jane Smith",
                        phoneNumber = "0987654321",
                        address = "456 Second Avenue",
                        ward = "Ward 2",
                        district = "District 2",
                        city = "Ho Chi Minh City"
                    )
                )

                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        shippingInfos = mockShippingInfos,
                        errorMessage = ""
                    )
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load shipping addresses: ${e.message}"
                    )
                }
            }
        }
    }

    private fun deleteShippingInfo(shippingInfoId: Long) {
        viewModelScope.launch {
            try {
                // TODO: Replace with actual repository call
                val updatedList = viewModelState.value.shippingInfos.filter { 
                    it.shippingInfoId != shippingInfoId 
                }
                
                viewModelState.update {
                    it.copy(
                        shippingInfos = updatedList,
                        errorMessage = ""
                    )
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        errorMessage = "Failed to delete shipping address: ${e.message}"
                    )
                }
            }
        }
    }
} 