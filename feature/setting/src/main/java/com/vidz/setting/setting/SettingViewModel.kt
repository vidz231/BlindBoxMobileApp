package com.vidz.setting.setting

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.datastore.AppPreferencesDataStore
import com.vidz.datastore.AppSettings
import com.vidz.datastore.Currency
import com.vidz.datastore.Language
import com.vidz.datastore.ThemeMode
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.model.Account
import com.vidz.domain.model.Transaction
import com.vidz.domain.model.TransactionStatus
import com.vidz.domain.model.TransactionType
import com.vidz.domain.usecase.GetUserLocalProfileInfoUseCase
import com.vidz.domain.usecase.IsAuthenticatedUseCase
import com.vidz.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appPreferencesDataStore: AppPreferencesDataStore,
    private val getUserLocalProfileInfoUseCase: GetUserLocalProfileInfoUseCase,
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<SettingEvent, SettingUiState, SettingViewModelState>(
    SettingViewModelState()
) {

    private var hasInitialized = false
    private var userProfileLoaded = false
    private var transactionsLoaded = false

    init {
        checkAuthenticationAndLoadData()
    }

    override fun onTriggerEvent(event: SettingEvent) {
        when (event) {
            is SettingEvent.OnTabChanged -> {
                updateSelectedTab(event.tab)
            }
            SettingEvent.OnRefreshTransactions -> {
                // Clear transactions first to allow reloading
                transactionsLoaded = false
                viewModelState.value = viewModelState.value.copy(
                    transactions = emptyList(),
                    filteredTransactions = emptyList()
                )
                loadTransactions()
            }
            is SettingEvent.OnThemeChanged -> {
                updateTheme(event.themeMode)
            }
            is SettingEvent.OnNotificationToggled -> {
                updateNotificationSetting(event.enabled)
            }
            is SettingEvent.OnPushNotificationToggled -> {
                updatePushNotificationSetting(event.enabled)
            }
            is SettingEvent.OnEmailNotificationToggled -> {
                updateEmailNotificationSetting(event.enabled)
            }
            is SettingEvent.OnBiometricAuthToggled -> {
                updateBiometricAuthSetting(event.enabled)
            }
            is SettingEvent.OnAutoSyncToggled -> {
                updateAutoSyncSetting(event.enabled)
            }
            is SettingEvent.OnLanguageChanged -> {
                updateLanguage(event.language)
            }
            is SettingEvent.OnCurrencyChanged -> {
                updateCurrency(event.currency)
            }
            is SettingEvent.OnTransactionFilterChanged -> {
                updateTransactionFilter(event.filter)
            }
            SettingEvent.OnRefreshAuth -> {
                // Reset initialization flags to allow re-checking authentication after login
                hasInitialized = false
                userProfileLoaded = false
                transactionsLoaded = false
                // Clear any existing data
                viewModelState.value = viewModelState.value.copy(
                    userProfile = null,
                    transactions = emptyList(),
                    filteredTransactions = emptyList(),
                    error = null
                )
                // Re-check authentication
                checkAuthenticationAndLoadData()
            }
            SettingEvent.OnLogoutClicked -> {
                performLogout()
            }
        }
    }

    private fun checkAuthenticationAndLoadData() {
        if (hasInitialized) return
        
        viewModelScope.launch {
            val currentState = viewModelState.value
            viewModelState.value = currentState.copy(isLoadingAuth = true)

            try {
                isAuthenticatedUseCase.invoke().take(1).collect { isAuthenticated ->
                    Log.d("SettingViewModel", "Authentication state: $isAuthenticated")
                    
                    viewModelState.update { currentState ->
                        currentState.copy(
                            isAuthenticated = isAuthenticated,
                            isLoadingAuth = false
                        )
                    }
                    
                    if (isAuthenticated) {
                        // Only load data if we haven't loaded it yet
                        val state = viewModelState.value
                        if (!userProfileLoaded && !transactionsLoaded && !hasInitialized) {
                            Log.d("SettingViewModel", "Loading user profile and transactions...")
                            hasInitialized = true
                            loadUserProfile()
                            loadTransactions()
                        } else {
                            Log.d("SettingViewModel", "Data already loaded or in progress, skipping...")
                        }
                    } else {
                        Log.d("SettingViewModel", "User not authenticated, clearing data")
                        hasInitialized = true
                        userProfileLoaded = false
                        transactionsLoaded = false
                        // User is not authenticated, clear data and stop loading
                        viewModelState.update { currentState ->
                            currentState.copy(
                                isLoading = false, 
                                isLoadingTransactions = false, 
                                isLoadingAuth = false,
                                userProfile = null,
                                transactions = emptyList(),
                                filteredTransactions = emptyList()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingViewModel", "Error checking authentication: ${e.message}")
                hasInitialized = true
                viewModelState.update { currentState ->
                    currentState.copy(
                        isLoadingAuth = false,
                        isAuthenticated = false,
                        error = e.message ?: "Authentication check failed"
                    )
                }
            }
        }
    }

    private fun updateSelectedTab(tab: SettingTab) {
        viewModelState.value = viewModelState.value.copy(
            selectedTab = tab
        )
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val currentState = viewModelState.value
            
            // Prevent multiple calls
            if (currentState.isLoading || userProfileLoaded) {
                Log.d("SettingViewModel", "Skipping loadUserProfile - already loading or loaded")
                return@launch
            }

            Log.d("SettingViewModel", "Starting loadUserProfile")
            viewModelState.value = currentState.copy(isLoading = true)

            try {
                getUserLocalProfileInfoUseCase.invoke()
                    .collect {
                        when (it) {
                            Init -> {
                                Log.d("SettingViewModel", "Initialization started")
                                // Don't update state here, keep loading = true
                            }

                            is ServerError.General -> {
                                Log.e("SettingViewModel", "General server error: ${it.message}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(isLoading = false, error = it.message)
                                }
                            }

                            is ServerError.MissingParam -> {
                                Log.e("SettingViewModel", "Missing parameter error: ${it.message}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(isLoading = false, error = it.message)
                                }
                            }

                            is ServerError.NotEnoughCredit -> {
                                Log.e("SettingViewModel", "Not enough credit error: ${it.message}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(isLoading = false, error = it.message)
                                }
                            }

                            is ServerError.RequiredLogin -> {
                                Log.e("SettingViewModel", "Required login error: ${it.message}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(isLoading = false, error = it.message)
                                }
                            }

                            is ServerError.RequiredVip -> {
                                Log.e("SettingViewModel", "Required VIP error: ${it.message}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(isLoading = false, error = it.message)
                                }
                            }

                            is ServerError.Token -> {
                                Log.e("SettingViewModel", "Token error: ${it.message}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(isLoading = false, error = it.message)
                                }
                            }

                            is ServerError.Internet -> {
                                Log.e("SettingViewModel", "Internet error: ${it.message}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(isLoading = false, error = it.message)
                                }
                            }

                            is Success<Account> -> {
                                Log.d("SettingViewModel", "Successfully fetched user profile: ${it.data}")
                                userProfileLoaded = true
                                viewModelState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        userProfile = it.data,
                                        )
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e("SettingViewModel", "Exception in loadUserProfile: ${e.message}")
                userProfileLoaded = true
                viewModelState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load user profile"
                    )
                }
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                val currentState = viewModelState.value
                
                // Prevent multiple calls
                if (currentState.isLoadingTransactions || transactionsLoaded) {
                    Log.d("SettingViewModel", "Skipping loadTransactions - already loading or loaded")
                    return@launch
                }
                
                Log.d("SettingViewModel", "Starting loadTransactions")
                viewModelState.value = currentState.copy(isLoadingTransactions = true)
                
                // TODO: Replace with actual repository call
                delay(1000)
                val mockTransactions = generateMockTransactions()
                
                Log.d("SettingViewModel", "Transactions loaded: ${mockTransactions.size} items")
                transactionsLoaded = true
                viewModelState.update { currentState ->
                    currentState.copy(
                        isLoadingTransactions = false,
                        transactions = mockTransactions,
                        filteredTransactions = applyTransactionFilter(mockTransactions, currentState.transactionFilter)
                    )
                }
            } catch (e: Exception) {
                Log.e("SettingViewModel", "Exception in loadTransactions: ${e.message}")
                transactionsLoaded = true
                viewModelState.update { currentState ->
                    currentState.copy(
                        isLoadingTransactions = false,
                        error = e.message ?: "Failed to load transactions"
                    )
                }
            }
        }
    }

    private fun updateTransactionFilter(filter: TransactionFilter) {
        val currentState = viewModelState.value
        viewModelState.value = currentState.copy(
            transactionFilter = filter,
            filteredTransactions = applyTransactionFilter(currentState.transactions, filter)
        )
    }

    private fun applyTransactionFilter(transactions: List<Transaction>, filter: TransactionFilter): List<Transaction> {
        return when (filter) {
            TransactionFilter.ALL -> transactions
            TransactionFilter.DEPOSITS -> transactions.filter { it.type == TransactionType.Deposit }
            TransactionFilter.ORDERS -> transactions.filter { it.type == TransactionType.Order }
            TransactionFilter.SUCCESS -> transactions.filter { it.status == TransactionStatus.Success }
            TransactionFilter.PENDING -> transactions.filter { it.status == TransactionStatus.Pending }
            TransactionFilter.FAILED -> transactions.filter { it.status == TransactionStatus.Failed }
        }
    }

    private fun updateTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            appPreferencesDataStore.updateThemeMode(themeMode)
        }
    }

    private fun updateNotificationSetting(enabled: Boolean) {
        viewModelScope.launch {
            appPreferencesDataStore.updateNotificationsEnabled(enabled)
        }
    }

    private fun updatePushNotificationSetting(enabled: Boolean) {
        viewModelScope.launch {
            appPreferencesDataStore.updatePushNotificationsEnabled(enabled)
        }
    }

    private fun updateEmailNotificationSetting(enabled: Boolean) {
        viewModelScope.launch {
            appPreferencesDataStore.updateEmailNotificationsEnabled(enabled)
        }
    }

    private fun updateBiometricAuthSetting(enabled: Boolean) {
        viewModelScope.launch {
            appPreferencesDataStore.updateBiometricAuthEnabled(enabled)
        }
    }

    private fun updateAutoSyncSetting(enabled: Boolean) {
        viewModelScope.launch {
            appPreferencesDataStore.updateAutoSyncEnabled(enabled)
        }
    }

    private fun updateLanguage(language: Language) {
        viewModelScope.launch {
            appPreferencesDataStore.updateLanguage(language.code)
        }
    }

    private fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            appPreferencesDataStore.updateCurrency(currency.code)
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            val currentState = viewModelState.value
            viewModelState.value = currentState.copy(isLoading = true)

            try {
                logoutUseCase.invoke().collect { result ->
                    when (result) {
                        Init -> {
                            Log.d("SettingViewModel", "Logout initialization started")
                        }
                        is Success -> {
                            Log.d("SettingViewModel", "Logout successful")
                            // Reset all state after successful logout
                            hasInitialized = false
                            userProfileLoaded = false
                            transactionsLoaded = false
                            viewModelState.value = SettingViewModelState(
                                isAuthenticated = false,
                                isLoading = false,
                                isLoadingAuth = false
                            )
                        }
                        is ServerError.General -> {
                            Log.e("SettingViewModel", "Logout failed: ${result.message}")
                            // Even if logout fails on server, clear local state
                            hasInitialized = false
                            userProfileLoaded = false
                            transactionsLoaded = false
                            viewModelState.value = SettingViewModelState(
                                isAuthenticated = false,
                                isLoading = false,
                                isLoadingAuth = false,
                                error = result.message
                            )
                        }
                        else -> {
                            Log.e("SettingViewModel", "Logout failed with unknown error")
                            // Even if logout fails, clear local state
                            hasInitialized = false
                            userProfileLoaded = false
                            transactionsLoaded = false
                            viewModelState.value = SettingViewModelState(
                                isAuthenticated = false,
                                isLoading = false,
                                isLoadingAuth = false,
                                error = "Logout failed"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingViewModel", "Exception during logout: ${e.message}")
                // Even if logout fails, clear local state
                hasInitialized = false
                userProfileLoaded = false
                transactionsLoaded = false
                viewModelState.value = SettingViewModelState(
                    isAuthenticated = false,
                    isLoading = false,
                    isLoadingAuth = false,
                    error = e.message ?: "Logout failed"
                )
            }
        }
    }

    private fun generateMockTransactions(): List<Transaction> {
        // TODO: Remove this mock data when real repository is implemented
        return listOf(
            Transaction(
                transactionId = 1,
                type = TransactionType.Deposit,
                amount = 100.0,
                oldBalance = 50.0,
                newBalance = 150.0,
                status = TransactionStatus.Success,
                createdAt = "2024-01-15T10:30:00Z"
            ),
            Transaction(
                transactionId = 2,
                type = TransactionType.Order,
                amount = -25.50,
                oldBalance = 150.0,
                newBalance = 124.50,
                status = TransactionStatus.Success,
                orderId = 1001,
                createdAt = "2024-01-14T14:20:00Z"
            ),
            Transaction(
                transactionId = 3,
                type = TransactionType.Deposit,
                amount = 50.0,
                oldBalance = 124.50,
                newBalance = 174.50,
                status = TransactionStatus.Pending,
                createdAt = "2024-01-13T09:15:00Z"
            ),
            Transaction(
                transactionId = 4,
                type = TransactionType.Order,
                amount = -15.75,
                oldBalance = 174.50,
                newBalance = 158.75,
                status = TransactionStatus.Failed,
                orderId = 1002,
                createdAt = "2024-01-12T16:45:00Z"
            )
        )
    }
}

data class SettingViewModelState(
    val selectedTab: SettingTab = SettingTab.TRANSACTIONS,
    val userProfile: Account? = null,
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val transactionFilter: TransactionFilter = TransactionFilter.ALL,
    val appSettings: AppSettings = AppSettings(),
    val isLoading: Boolean = false,
    val isLoadingTransactions: Boolean = false,
    val error: String? = null,
    val isLoadingAuth: Boolean = false,
    val isAuthenticated: Boolean = false
) : ViewModelState() {
    override fun toUiState(): ViewState = SettingUiState(
        selectedTab = selectedTab,
        userProfile = userProfile,
        transactions = filteredTransactions,
        transactionFilter = transactionFilter,
        appSettings = appSettings,
        isLoading = isLoading,
        isLoadingTransactions = isLoadingTransactions,
        error = error,
        isLoadingAuth = isLoadingAuth,
        isAuthenticated = isAuthenticated
    )
}

data class SettingUiState(
    val selectedTab: SettingTab = SettingTab.TRANSACTIONS,
    val userProfile: Account? = null,
    val transactions: List<Transaction> = emptyList(),
    val transactionFilter: TransactionFilter = TransactionFilter.ALL,
    val appSettings: AppSettings = AppSettings(),
    val isLoading: Boolean = false,
    val isLoadingTransactions: Boolean = false,
    val error: String? = null,
    val isLoadingAuth: Boolean = false,
    val isAuthenticated: Boolean = false
) : ViewState()

sealed class SettingEvent : ViewEvent {
    data class OnTabChanged(val tab: SettingTab) : SettingEvent()
    data object OnRefreshTransactions : SettingEvent()
    data class OnThemeChanged(val themeMode: ThemeMode) : SettingEvent()
    data class OnNotificationToggled(val enabled: Boolean) : SettingEvent()
    data class OnPushNotificationToggled(val enabled: Boolean) : SettingEvent()
    data class OnEmailNotificationToggled(val enabled: Boolean) : SettingEvent()
    data class OnBiometricAuthToggled(val enabled: Boolean) : SettingEvent()
    data class OnAutoSyncToggled(val enabled: Boolean) : SettingEvent()
    data class OnLanguageChanged(val language: Language) : SettingEvent()
    data class OnCurrencyChanged(val currency: Currency) : SettingEvent()
    data class OnTransactionFilterChanged(val filter: TransactionFilter) : SettingEvent()
    data object OnRefreshAuth : SettingEvent()
    data object OnLogoutClicked : SettingEvent()
}

enum class SettingTab(val displayName: String) {
    TRANSACTIONS("Transactions"),
    APP_SETTINGS("App Settings")
}

enum class TransactionFilter(val displayName: String) {
    ALL("All"),
    DEPOSITS("Deposits"),
    ORDERS("Orders"),
    SUCCESS("Success"),
    PENDING("Pending"),
    FAILED("Failed")
} 