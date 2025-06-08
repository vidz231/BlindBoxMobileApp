package com.vidz.setting.edit_profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.model.Account
import com.vidz.domain.model.BlindBox
import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.usecase.GetBlindBoxesUseCase
import com.vidz.domain.usecase.GetUserLocalProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserLocalProfileInfoUseCase: GetUserLocalProfileInfoUseCase,

) : BaseViewModel<EditProfileEvent, EditProfileUiState, EditProfileViewModelState>(
    EditProfileViewModelState()
) {

    init {
        loadUserProfile()
    }

    override fun onTriggerEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.OnFirstNameChanged -> {
                updateFirstName(event.firstName)
            }
            is EditProfileEvent.OnLastNameChanged -> {
                updateLastName(event.lastName)
            }
            is EditProfileEvent.OnEmailChanged -> {
                updateEmail(event.email)
            }
            is EditProfileEvent.OnCurrentPasswordChanged -> {
                updateCurrentPassword(event.password)
            }
            is EditProfileEvent.OnNewPasswordChanged -> {
                updateNewPassword(event.password)
            }
            is EditProfileEvent.OnConfirmPasswordChanged -> {
                updateConfirmPassword(event.password)
            }
            is EditProfileEvent.OnFullNameChanged -> {
                updateFullName(event.fullName)
            }
            is EditProfileEvent.OnPhoneNumberChanged -> {
                updatePhoneNumber(event.phoneNumber)
            }
            is EditProfileEvent.OnAddressChanged -> {
                updateAddress(event.address)
            }
            is EditProfileEvent.OnCityChanged -> {
                updateCity(event.city)
            }
            is EditProfileEvent.OnPostalCodeChanged -> {
                updatePostalCode(event.postalCode)
            }
            is EditProfileEvent.OnCountryChanged -> {
                updateCountry(event.country)
            }
            EditProfileEvent.OnSaveClicked -> {
                saveProfile()
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val currentState = viewModelState.value

            viewModelState.value = currentState.copy(isLoading = true)

            try {
                getUserLocalProfileInfoUseCase.invoke()
                    .collect {
                    when (it) {
                        Init -> {
                            Log.d("EditProfileViewModel", "Initialization started")
                            viewModelState.update { state ->
                                state.copy(isLoading = true, error = null)
                            }
                        }

                        is ServerError.General -> {
                            Log.e("EditProfileViewModel", "General server error: ${it.message}")

                        }

                        is ServerError.MissingParam -> {
                            Log.e("EditProfileViewModel", "Missing parameter error: ${it.message}")
                        }

                        is ServerError.NotEnoughCredit ->
                            Log.e("EditProfileViewModel", "Not enough credit error: ${it.message}")

                        is ServerError.RequiredLogin ->
                            Log.e("EditProfileViewModel", "Required login error: ${it.message}")

                        is ServerError.RequiredVip ->
                            Log.e("EditProfileViewModel", "Required VIP error: ${it.message}")

                        is ServerError.Token ->
                            Log.e("EditProfileViewModel", "Token error: ${it.message}")

                        is ServerError.Internet
                            -> Log.e("EditProfileViewModel", "Internet error: ${it.message}")

                        is Success<Account> -> {
                            Log.d("EditProfileViewModel", "Successfully fetched blind boxes: ${it.data}")

                            viewModelState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    originalAccount = it.data,
                                    firstName = it.data.firstName ?: "",
                                    lastName = it.data.lastName ?: "",
                                    email = it.data.email ?: "",





                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                viewModelState.value = currentState.copy(
                    isLoading = false,
                    error = e.message ?: "Search failed"
                )
            }
        }
    }

    private fun updateFirstName(firstName: String) {
        viewModelState.value = viewModelState.value.copy(
            firstName = firstName,
            firstNameError = null
        )
        validateForm()
    }

    private fun updateLastName(lastName: String) {
        viewModelState.value = viewModelState.value.copy(
            lastName = lastName,
            lastNameError = null
        )
        validateForm()
    }

    private fun updateEmail(email: String) {
        viewModelState.value = viewModelState.value.copy(
            email = email,
            emailError = null
        )
        validateForm()
    }

    private fun updateCurrentPassword(password: String) {
        viewModelState.value = viewModelState.value.copy(
            currentPassword = password,
            currentPasswordError = null
        )
        validateForm()
    }

    private fun updateNewPassword(password: String) {
        viewModelState.value = viewModelState.value.copy(
            newPassword = password,
            newPasswordError = null,
            confirmPasswordError = null
        )
        validateForm()
    }

    private fun updateConfirmPassword(password: String) {
        viewModelState.value = viewModelState.value.copy(
            confirmPassword = password,
            confirmPasswordError = null
        )
        validateForm()
    }

    private fun updateFullName(fullName: String) {
        viewModelState.value = viewModelState.value.copy(
            fullName = fullName,
            fullNameError = null
        )
        validateForm()
    }

    private fun updatePhoneNumber(phoneNumber: String) {
        viewModelState.value = viewModelState.value.copy(
            phoneNumber = phoneNumber,
            phoneNumberError = null
        )
        validateForm()
    }

    private fun updateAddress(address: String) {
        viewModelState.value = viewModelState.value.copy(
            address = address,
            addressError = null
        )
        validateForm()
    }

    private fun updateCity(city: String) {
        viewModelState.value = viewModelState.value.copy(
            city = city,
            cityError = null
        )
        validateForm()
    }

    private fun updatePostalCode(postalCode: String) {
        viewModelState.value = viewModelState.value.copy(
            postalCode = postalCode,
            postalCodeError = null
        )
        validateForm()
    }

    private fun updateCountry(country: String) {
        viewModelState.value = viewModelState.value.copy(
            country = country,
            countryError = null
        )
        validateForm()
    }

    private fun validateForm() {
        val currentState = viewModelState.value
        var isValid = true

        // Validate required fields
        val firstNameError = if (currentState.firstName.isBlank()) {
            isValid = false
            "First name is required"
        } else null

        val lastNameError = if (currentState.lastName.isBlank()) {
            isValid = false
            "Last name is required"
        } else null

        val emailError = if (currentState.email.isBlank()) {
            isValid = false
            "Email is required"
        } else if (!isValidEmail(currentState.email)) {
            isValid = false
            "Invalid email format"
        } else null

        val currentPasswordError = if (currentState.currentPassword.isBlank()) {
            isValid = false
            "Current password is required"
        } else null

        val newPasswordError = if (currentState.newPassword.isNotEmpty() && currentState.newPassword.length < 6) {
            isValid = false
            "Password must be at least 6 characters"
        } else null

        val confirmPasswordError = if (currentState.newPassword.isNotEmpty() && 
            currentState.newPassword != currentState.confirmPassword) {
            isValid = false
            "Passwords do not match"
        } else null

        val fullNameError = if (currentState.fullName.isBlank()) {
            isValid = false
            "Full name is required"
        } else null

        val phoneNumberError = if (currentState.phoneNumber.isBlank()) {
            isValid = false
            "Phone number is required"
        } else if (!isValidPhoneNumber(currentState.phoneNumber)) {
            isValid = false
            "Invalid phone number format"
        } else null

        val addressError = if (currentState.address.isBlank()) {
            isValid = false
            "Address is required"
        } else null

        val cityError = if (currentState.city.isBlank()) {
            isValid = false
            "City is required"
        } else null

        val postalCodeError = if (currentState.postalCode.isBlank()) {
            isValid = false
            "Postal code is required"
        } else null

        val countryError = if (currentState.country.isBlank()) {
            isValid = false
            "Country is required"
        } else null

        viewModelState.value = currentState.copy(
            isFormValid = isValid,
            firstNameError = firstNameError,
            lastNameError = lastNameError,
            emailError = emailError,
            currentPasswordError = currentPasswordError,
            newPasswordError = newPasswordError,
            confirmPasswordError = confirmPasswordError,
            fullNameError = fullNameError,
            phoneNumberError = phoneNumberError,
            addressError = addressError,
            cityError = cityError,
            postalCodeError = postalCodeError,
            countryError = countryError
        )
    }

    private fun saveProfile() {
        if (!viewModelState.value.isFormValid) return

        viewModelScope.launch {
            try {
                val currentState = viewModelState.value
                viewModelState.value = currentState.copy(
                    isLoading = true,
                    error = null
                )



                viewModelState.value = currentState.copy(
                    isLoading = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                viewModelState.value = viewModelState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save profile"
                )
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^[+]?[0-9]{10,15}$"))
    }
}

data class EditProfileViewModelState(
    val isLoading: Boolean = true,
    val isFormValid: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val originalAccount: Account? = null,
    
    // Personal Information
    val firstName: String = "",
    val lastName: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    
    // Account Information
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    
    // Shipping Information
    val fullName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = "",
    val fullNameError: String? = null,
    val phoneNumberError: String? = null,
    val addressError: String? = null,
    val cityError: String? = null,
    val postalCodeError: String? = null,
    val countryError: String? = null
) : ViewModelState() {
    override fun toUiState(): ViewState = EditProfileUiState(
        isLoading = isLoading,
        isFormValid = isFormValid,
        isSaved = isSaved,
        error = error,
        firstName = firstName,
        lastName = lastName,
        firstNameError = firstNameError,
        lastNameError = lastNameError,
        email = email,
        currentPassword = currentPassword,
        newPassword = newPassword,
        confirmPassword = confirmPassword,
        emailError = emailError,
        currentPasswordError = currentPasswordError,
        newPasswordError = newPasswordError,
        confirmPasswordError = confirmPasswordError,
        fullName = fullName,
        phoneNumber = phoneNumber,
        address = address,
        city = city,
        postalCode = postalCode,
        country = country,
        fullNameError = fullNameError,
        phoneNumberError = phoneNumberError,
        addressError = addressError,
        cityError = cityError,
        postalCodeError = postalCodeError,
        countryError = countryError
    )
}

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isFormValid: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    
    // Personal Information
    val firstName: String = "",
    val lastName: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    
    // Account Information
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    
    // Shipping Information
    val fullName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = "",
    val fullNameError: String? = null,
    val phoneNumberError: String? = null,
    val addressError: String? = null,
    val cityError: String? = null,
    val postalCodeError: String? = null,
    val countryError: String? = null
) : ViewState()

sealed class EditProfileEvent : ViewEvent {
    data class OnFirstNameChanged(val firstName: String) : EditProfileEvent()
    data class OnLastNameChanged(val lastName: String) : EditProfileEvent()
    data class OnEmailChanged(val email: String) : EditProfileEvent()
    data class OnCurrentPasswordChanged(val password: String) : EditProfileEvent()
    data class OnNewPasswordChanged(val password: String) : EditProfileEvent()
    data class OnConfirmPasswordChanged(val password: String) : EditProfileEvent()
    data class OnFullNameChanged(val fullName: String) : EditProfileEvent()
    data class OnPhoneNumberChanged(val phoneNumber: String) : EditProfileEvent()
    data class OnAddressChanged(val address: String) : EditProfileEvent()
    data class OnCityChanged(val city: String) : EditProfileEvent()
    data class OnPostalCodeChanged(val postalCode: String) : EditProfileEvent()
    data class OnCountryChanged(val country: String) : EditProfileEvent()
    data object OnSaveClicked : EditProfileEvent()
} 