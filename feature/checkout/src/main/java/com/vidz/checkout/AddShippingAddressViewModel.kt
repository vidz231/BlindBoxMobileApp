package com.vidz.checkout

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Success
import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.model.map.AutoComplete
import com.vidz.domain.usecase.CreateShippingInfoUseCase
import com.vidz.domain.usecase.GetAutoCompleteUseCase
import com.vidz.domain.usecase.GetPlaceDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.vidz.domain.Success as DomainSuccess

@HiltViewModel
class AddShippingAddressViewModel @Inject constructor(
    private val createShippingInfoUseCase: CreateShippingInfoUseCase,
    private val getAutoCompleteUseCase: GetAutoCompleteUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase
) : BaseViewModel<
        AddShippingAddressViewModel.AddShippingAddressViewEvent,
        AddShippingAddressViewModel.AddShippingAddressViewState,
        AddShippingAddressViewModel.AddShippingAddressViewModelState
        >(AddShippingAddressViewModelState()) {


    //region Event / ViewState / ViewModelState
    sealed class AddShippingAddressViewEvent : ViewEvent {
        data class UpdateName(val value: String) : AddShippingAddressViewEvent()
        data class UpdatePhone(val value: String) : AddShippingAddressViewEvent()
        data class UpdateAddress(val value: String) : AddShippingAddressViewEvent()
        data class UpdateWard(val value: String) : AddShippingAddressViewEvent()
        data class UpdateDistrict(val value: String) : AddShippingAddressViewEvent()
        data class UpdateCity(val value: String) : AddShippingAddressViewEvent()
        data class SearchAddress(val query: String) : AddShippingAddressViewEvent()
        data class SelectAutoCompleteSuggestion(val suggestion: AutoComplete) : AddShippingAddressViewEvent()
        object Save : AddShippingAddressViewEvent()
    }

    data class AddShippingAddressViewState(
        val name: String = "",
        val phone: String = "",
        val address: String = "",
        val ward: String = "",
        val district: String = "",
        val city: String = "",
        val searchQuery: String = "",
        val autoCompleteSuggestions: List<AutoComplete> = emptyList(),
        val isLoadingSuggestions: Boolean = false,
        val isSaving: Boolean = false,
        val errorMessage: String? = null,
        val success: Boolean = false
    ) : ViewState()

    data class AddShippingAddressViewModelState(
        val name: String = "",
        val phone: String = "",
        val address: String = "",
        val ward: String = "",
        val district: String = "",
        val city: String = "",
        val searchQuery: String = "",
        val autoCompleteSuggestions: List<AutoComplete> = emptyList(),
        val isLoadingSuggestions: Boolean = false,
        val isSaving: Boolean = false,
        val errorMessage: String? = null,
        val success: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return AddShippingAddressViewState(
                name = name,
                phone = phone,
                address = address,
                ward = ward,
                district = district,
                city = city,
                searchQuery = searchQuery,
                autoCompleteSuggestions = autoCompleteSuggestions,
                isLoadingSuggestions = isLoadingSuggestions,
                isSaving = isSaving,
                errorMessage = errorMessage,
                success = success
            )
        }
    }
    //endregion

    override fun onTriggerEvent(event: AddShippingAddressViewEvent) {
        when (event) {
            is AddShippingAddressViewEvent.UpdateName -> viewModelState.update { it.copy(name = event.value) }
            is AddShippingAddressViewEvent.UpdatePhone -> viewModelState.update { it.copy(phone = event.value) }
            is AddShippingAddressViewEvent.UpdateAddress -> viewModelState.update { it.copy(address = event.value) }
            is AddShippingAddressViewEvent.UpdateWard -> viewModelState.update { it.copy(ward = event.value) }
            is AddShippingAddressViewEvent.UpdateDistrict -> viewModelState.update { it.copy(district = event.value) }
            is AddShippingAddressViewEvent.UpdateCity -> viewModelState.update { it.copy(city = event.value) }
            is AddShippingAddressViewEvent.SearchAddress -> searchAddress(event.query)
            is AddShippingAddressViewEvent.SelectAutoCompleteSuggestion -> selectAutoCompleteSuggestion(event.suggestion)
            is AddShippingAddressViewEvent.Save -> saveShippingInfo()
        }
    }

    private fun saveShippingInfo() {
        val state = viewModelState.value

        val hasEmptyRequired = state.name.isBlank() ||
                state.phone.isBlank() ||
                state.address.isBlank() ||
                state.ward.isBlank() ||
                state.district.isBlank() ||
                state.city.isBlank()

        if (hasEmptyRequired) {
            viewModelState.update { it.copy(
                errorMessage = "Please fill out all required fields before saving."
            ) }
            return
        }

        val shipping = ShippingInfo(
            name = state.name,
            phoneNumber = state.phone,
            address = state.address,
            ward = state.ward,
            district = state.district,
            city = state.city
        )

        viewModelScope.launch {
            viewModelState.update { it.copy(isSaving = true, errorMessage = null) }
            createShippingInfoUseCase(shipping).collect { result ->
                when (result) {
                    is Success -> viewModelState.update { it.copy(isSaving = false, success = true) }
                    else -> viewModelState.update { it.copy(isSaving = false, errorMessage = "Failed to save address") }
                }
            }
        }
    }

    private fun searchAddress(query: String) {
        viewModelState.update { it.copy(
            searchQuery = query,
            isLoadingSuggestions = true
        ) }

        if (query.isBlank()) {
            viewModelState.update { it.copy(
                autoCompleteSuggestions = emptyList(),
                isLoadingSuggestions = false
            ) }
            return
        }

        viewModelScope.launch {
            getAutoCompleteUseCase(
                input = query
            ).collect { result ->
                when (result) {
                    is DomainSuccess -> {
                        viewModelState.update { it.copy(
                            autoCompleteSuggestions = result.data.predictions,
                            isLoadingSuggestions = false
                        ) }
                    }
                    else -> {
                        viewModelState.update { it.copy(
                            autoCompleteSuggestions = emptyList(),
                            isLoadingSuggestions = false
                        ) }
                    }
                }
            }
        }
    }

    private fun selectAutoCompleteSuggestion(suggestion: AutoComplete) {
        viewModelState.update { it.copy(
            searchQuery = suggestion.description,
            autoCompleteSuggestions = emptyList()
        ) }

        viewModelScope.launch {
            getPlaceDetailUseCase(
                placeId = suggestion.placeId
            ).collect { result ->
                when (result) {
                    is DomainSuccess -> {
                        val placeDetail = result.data.result
                        placeDetail?.let { detail ->
                            val addressParts = detail.address.split(", ")
                            val components = parseAddressComponents(addressParts)
                            viewModelState.update { it.copy(
                                address = components.address,
                                ward = components.ward,
                                district = components.district,
                                city = components.city
                            ) }
                        }
                    }
                    else -> {
                        viewModelState.update { it.copy(
                            address = suggestion.description
                        ) }
                    }
                }
            }
        }
    }

    private data class AddressComponents(
        val address: String,
        val ward: String,
        val district: String,
        val city: String
    )

    private fun parseAddressComponents(addressParts: List<String>): AddressComponents {
        return when {
            addressParts.size >= 4 -> AddressComponents(
                address = addressParts[0],
                ward = addressParts[1],
                district = addressParts[2],
                city = addressParts.drop(3).joinToString(", ")
            )
            addressParts.size == 3 -> AddressComponents(
                address = addressParts[0],
                ward = "",
                district = addressParts[1],
                city = addressParts[2]
            )
            addressParts.size == 2 -> AddressComponents(
                address = addressParts[0],
                ward = "",
                district = "",
                city = addressParts[1]
            )
            else -> AddressComponents(
                address = addressParts.joinToString(", "),
                ward = "",
                district = "",
                city = ""
            )
        }
    }

    fun onMapPointSelected(point: com.mapbox.geojson.Point, context: Context) {
        viewModelScope.launch {
            val address = com.vidz.base.components.reverseGeocode(context, point)
            address?.let { addr ->
                viewModelState.update { current -> current.copy(
                    address = addr.thoroughfare ?: "",
                    ward = addr.subLocality ?: "",
                    district = addr.subAdminArea ?: "",
                    city = addr.locality ?: addr.adminArea ?: ""
                ) }
            }
        }
    }
} 