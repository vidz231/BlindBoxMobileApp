package com.vidz.setting

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vidz.base.components.LoginPromptComponent
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.datastore.Currency
import com.vidz.datastore.Language
import com.vidz.datastore.ThemeMode
import com.vidz.domain.model.Account
import com.vidz.domain.model.Transaction
import com.vidz.domain.model.TransactionStatus
import com.vidz.domain.model.TransactionType
import com.vidz.setting.setting.SettingEvent
import com.vidz.setting.setting.SettingTab
import com.vidz.setting.setting.SettingUiState
import com.vidz.setting.setting.SettingViewModel
import com.vidz.setting.setting.TransactionFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Refresh authentication when screen is first composed or when coming back from login
    DisposableEffect(Unit) {
        Log.d("SettingScreen", "Screen attached, refreshing auth state")
        viewModel.onTriggerEvent(SettingEvent.OnRefreshAuth)
        onDispose {
            Log.d("SettingScreen", "Screen detached")
        }
    }

    val onNavigateToLogin = {
        navController.navigate(DestinationRoutes.ROOT_LOGIN_SCREEN_ROUTE) {
            // Optional: Clear the back stack or handle navigation as needed
        }
    }

    val onNavigateToEditProfile = {
        navController.navigate(DestinationRoutes.ROOT_EDIT_PROFILE_SCREEN_ROUTE) {
            // Optional: Clear the back stack or handle navigation as needed
        }
    }
    
    val onRefreshAuth = {
        Log.d("SettingScreen", "Refreshing authentication state")
        viewModel.onTriggerEvent(SettingEvent.OnRefreshAuth)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if(uiState.isLoadingAuth) {
            Log.d("SettingScreen", "Loading auth...")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (!uiState.isAuthenticated) {
            Log.d("SettingScreen", "User not authenticated, showing login prompt")
            LoginPromptComponent(
                onLoginClick = onNavigateToLogin,
                modifier = Modifier.padding(paddingValues),
                title = "Please Login",
                subtitle = "You need to login to access your settings and transactions"
            )
        } else if (uiState.isLoading) {
            Log.d("SettingScreen", "Loading settings...")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            Log.d("SettingScreen", "Displaying settings screen")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // User Profile Section
                UserProfileSection(
                    user = uiState.userProfile,
                    onEditProfile = {
                        Log.d("SettingScreen", "Navigating to edit profile")
                        onNavigateToEditProfile()
                    }
                )

                // Tabs
                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SettingTab.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.onTriggerEvent(SettingEvent.OnTabChanged(tab)) },
                            text = { Text(tab.displayName) }
                        )
                    }
                }

                // Tab Content
                when (uiState.selectedTab) {
                    SettingTab.TRANSACTIONS -> {
                        TransactionsTab(
                            transactions = uiState.transactions,
                            selectedFilter = uiState.transactionFilter,
                            isLoading = uiState.isLoadingTransactions,
                            onFilterChanged = { viewModel.onTriggerEvent(SettingEvent.OnTransactionFilterChanged(it)) },
                            onRefresh = { viewModel.onTriggerEvent(SettingEvent.OnRefreshTransactions) },
                        )
                    }
                    SettingTab.APP_SETTINGS -> {
                        AppSettingsTab(
                            appSettings = uiState.appSettings,
                            onThemeChanged = { viewModel.onTriggerEvent(SettingEvent.OnThemeChanged(it)) },
                            onNotificationToggled = { viewModel.onTriggerEvent(SettingEvent.OnNotificationToggled(it)) },
                            onPushNotificationToggled = { viewModel.onTriggerEvent(SettingEvent.OnPushNotificationToggled(it)) },
                            onEmailNotificationToggled = { viewModel.onTriggerEvent(SettingEvent.OnEmailNotificationToggled(it)) },
                            onBiometricAuthToggled = { viewModel.onTriggerEvent(SettingEvent.OnBiometricAuthToggled(it)) },
                            onAutoSyncToggled = { viewModel.onTriggerEvent(SettingEvent.OnAutoSyncToggled(it)) },
                            onLanguageChanged = { viewModel.onTriggerEvent(SettingEvent.OnLanguageChanged(it)) },
                            onCurrencyChanged = { viewModel.onTriggerEvent(SettingEvent.OnCurrencyChanged(it)) },
                            onLogout = { viewModel.onTriggerEvent(SettingEvent.OnLogoutClicked) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserProfileSection(
    user: Account?,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onEditProfile) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    if (user != null) {
                        Text(
                            text = "${user.firstName} ${user.lastName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Balance: $${String.format("%.2f", user.balance)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        if (user.isVerified) {
                            Text(
                                text = "✓ Verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Text(
                            text = "Loading profile...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionsTab(
    transactions: List<Transaction>,
    selectedFilter: TransactionFilter,
    isLoading: Boolean,
    onFilterChanged: (TransactionFilter) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transaction History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TransactionFilter.entries) { filter ->
                FilterChip(
                    onClick = { onFilterChanged(filter) },
                    label = { Text(filter.displayName) },
                    selected = selectedFilter == filter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            TransactionList(transactions = transactions)
        }
    }
}

@Composable
private fun TransactionList(transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No transactions found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (transaction.type) {
                            TransactionType.Deposit -> "Deposit"
                            TransactionType.Order -> "Order #${transaction.orderId}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = formatDate(transaction.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${if (transaction.amount >= 0) "+" else ""}$${String.format("%.2f", transaction.amount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            transaction.amount > 0 -> MaterialTheme.colorScheme.primary
                            transaction.amount < 0 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    Text(
                        text = when (transaction.status) {
                            TransactionStatus.Success -> "Success"
                            TransactionStatus.Pending -> "Pending"
                            TransactionStatus.Failed -> "Failed"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (transaction.status) {
                            TransactionStatus.Success -> MaterialTheme.colorScheme.primary
                            TransactionStatus.Pending -> MaterialTheme.colorScheme.tertiary
                            TransactionStatus.Failed -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppSettingsTab(
    appSettings: com.vidz.datastore.AppSettings,
    onThemeChanged: (ThemeMode) -> Unit,
    onNotificationToggled: (Boolean) -> Unit,
    onPushNotificationToggled: (Boolean) -> Unit,
    onEmailNotificationToggled: (Boolean) -> Unit,
    onBiometricAuthToggled: (Boolean) -> Unit,
    onAutoSyncToggled: (Boolean) -> Unit,
    onLanguageChanged: (Language) -> Unit,
    onCurrencyChanged: (Currency) -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 68.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SettingSection(title = "Appearance") {
                DropdownSetting(
                    title = "Theme",
                    subtitle = "Choose your preferred theme",
                    currentValue = appSettings.themeMode.name,
                    options = ThemeMode.entries.map { it.name },
                    onValueChanged = { value ->
                        onThemeChanged(ThemeMode.valueOf(value))
                    }
                )
            }
        }

        item {
            SettingSection(title = "Notifications") {
                SwitchSetting(
                    title = "Enable Notifications",
                    subtitle = "Receive app notifications",
                    checked = appSettings.notificationsEnabled,
                    onCheckedChange = onNotificationToggled
                )
                
                SwitchSetting(
                    title = "Push Notifications",
                    subtitle = "Receive push notifications",
                    checked = appSettings.pushNotificationsEnabled,
                    onCheckedChange = onPushNotificationToggled,
                    enabled = appSettings.notificationsEnabled
                )
                
                SwitchSetting(
                    title = "Email Notifications",
                    subtitle = "Receive email notifications",
                    checked = appSettings.emailNotificationsEnabled,
                    onCheckedChange = onEmailNotificationToggled,
                    enabled = appSettings.notificationsEnabled
                )
            }
        }

        item {
            SettingSection(title = "Security") {
                SwitchSetting(
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face unlock",
                    checked = appSettings.biometricAuthEnabled,
                    onCheckedChange = onBiometricAuthToggled
                )
            }
        }

        item {
            SettingSection(title = "Data & Sync") {
                SwitchSetting(
                    title = "Auto Sync",
                    subtitle = "Automatically sync data",
                    checked = appSettings.autoSyncEnabled,
                    onCheckedChange = onAutoSyncToggled
                )
            }
        }

        item {
            SettingSection(title = "Preferences") {
                DropdownSetting(
                    title = "Language",
                    subtitle = "Choose your language",
                    currentValue = Language.entries.find { it.code == appSettings.language }?.displayName ?: "English",
                    options = Language.entries.map { it.displayName },
                    onValueChanged = { displayName ->
                        Language.entries.find { it.displayName == displayName }?.let {
                            onLanguageChanged(it)
                        }
                    }
                )
                
                DropdownSetting(
                    title = "Currency",
                    subtitle = "Choose your currency",
                    currentValue = Currency.entries.find { it.code == appSettings.currency }?.displayName ?: "US Dollar",
                    options = Currency.entries.map { it.displayName },
                    onValueChanged = { displayName ->
                        Currency.entries.find { it.displayName == displayName }?.let {
                            onCurrencyChanged(it)
                        }
                    }
                )
            }
        }

        item {
            SettingSection(title = "Account") {
                LogoutButton(onLogout = onLogout)
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            content()
        }
    }
}

@Composable
private fun SwitchSetting(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun DropdownSetting(
    title: String,
    subtitle: String,
    currentValue: String,
    options: List<String>,
    onValueChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChanged(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LogoutButton(
    onLogout: () -> Unit
) {
    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            Icons.Default.ExitToApp,
            contentDescription = "Logout",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Sign Out",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}