package com.vidz.checkout

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Success
import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.usecase.GetShippingInfosUseCase
import com.vidz.domain.usecase.GetShippingInfoByIdUseCase
import com.vidz.domain.usecase.CreateShippingInfoUseCase
import com.vidz.domain.usecase.UpdateShippingInfoUseCase
import com.vidz.domain.usecase.DeleteShippingInfoUseCase
import com.vidz.domain.usecase.IsAuthenticatedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShippingSelectionViewModel @Inject constructor(
    private val getShippingInfosUseCase: GetShippingInfosUseCase,
    private val getShippingInfoByIdUseCase: GetShippingInfoByIdUseCase,
    private val createShippingInfoUseCase: CreateShippingInfoUseCase,
    private val updateShippingInfoUseCase: UpdateShippingInfoUseCase,
    private val deleteShippingInfoUseCase: DeleteShippingInfoUseCase,
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase
) : BaseViewModel<
        ShippingSelectionViewModel.ShippingSelectionViewEvent,
        ShippingSelectionViewModel.ShippingSelectionViewState,
        ShippingSelectionViewModel.ShippingSelectionViewModelState
        >(ShippingSelectionViewModelState()) {

    sealed class ShippingSelectionViewEvent : ViewEvent {
        data object LoadShippingInfos : ShippingSelectionViewEvent()
        data class GetShippingInfoById(val shippingInfoId: Long) : ShippingSelectionViewEvent()
        data class DeleteShippingInfo(val shippingInfoId: Long) : ShippingSelectionViewEvent()
        data object RefreshShippingInfos : ShippingSelectionViewEvent()
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
            is ShippingSelectionViewEvent.GetShippingInfoById -> getShippingInfoById(event.shippingInfoId)
            is ShippingSelectionViewEvent.DeleteShippingInfo -> deleteShippingInfo(event.shippingInfoId)
            is ShippingSelectionViewEvent.RefreshShippingInfos -> loadShippingInfos()
        }
    }

    private fun loadShippingInfos() {
        viewModelScope.launch {
            try {
                viewModelState.update { it.copy(isLoading = true) }
                
                // Check if user is authenticated first
                isAuthenticatedUseCase().collect { isAuthenticated ->
                    if (!isAuthenticated) {
                        // User is not authenticated, show mock data with a message
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
                                errorMessage = "Please log in to view your saved addresses. Showing sample addresses for now."
                            )
                        }
                        return@collect
                    }
                    
                    // User is authenticated, fetch real data from API
                    getShippingInfosUseCase(
                        accountId = 1L, // Note: API uses bearer token to identify user automatically
                        page = 0,
                        size = 50
                    ).collect { result ->
                        when (result) {
                            is Success -> {
                                viewModelState.update {
                                    it.copy(
                                        isLoading = false,
                                        shippingInfos = result.data,
                                        errorMessage = ""
                                    )
                                }
                            }
                            else -> {
                                // API call failed, fall back to mock data
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
                                        errorMessage = "Unable to load your addresses. Showing sample addresses."
                                    )
                                }
                            }
                        }
                    }
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

    private fun getShippingInfoById(shippingInfoId: Long) {
        viewModelScope.launch {
            try {
                getShippingInfoByIdUseCase(shippingInfoId).collect { result ->
                    when (result) {
                        is Success -> {
                            // Update the specific shipping info in the list
                            val updatedList = viewModelState.value.shippingInfos.map { existing ->
                                if (existing.shippingInfoId == shippingInfoId) {
                                    result.data
                                } else {
                                    existing
                                }
                            }
                            
                            viewModelState.update {
                                it.copy(
                                    shippingInfos = updatedList,
                                    errorMessage = ""
                                )
                            }
                        }
                        else -> {
                            viewModelState.update {
                                it.copy(
                                    errorMessage = "Failed to load shipping address details"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        errorMessage = "Failed to load shipping address: ${e.message}"
                    )
                }
            }
        }
    }

    private fun deleteShippingInfo(shippingInfoId: Long) {
        viewModelScope.launch {
            try {
                deleteShippingInfoUseCase(shippingInfoId).collect { result ->
                    when (result) {
                        is Success -> {
                            val updatedList = viewModelState.value.shippingInfos.filter { 
                                it.shippingInfoId != shippingInfoId 
                            }
                            
                            viewModelState.update {
                                it.copy(
                                    shippingInfos = updatedList,
                                    errorMessage = ""
                                )
                            }
                        }
                        else -> {
                            viewModelState.update {
                                it.copy(
                                    errorMessage = "Failed to delete shipping address"
                                )
                            }
                        }
                    }
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