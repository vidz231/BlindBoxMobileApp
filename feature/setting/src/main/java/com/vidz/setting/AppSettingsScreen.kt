package com.vidz.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vidz.base.components.LoginPromptComponent
import com.vidz.base.components.TopAppBarWithBack
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.datastore.Currency
import com.vidz.datastore.Language
import com.vidz.datastore.ThemeMode
import com.vidz.setting.setting.SettingEvent
import com.vidz.setting.setting.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Refresh authentication when screen is first composed
    DisposableEffect(Unit) {
        viewModel.onTriggerEvent(SettingEvent.OnRefreshAuth)
        onDispose { }
    }

    val onNavigateToLogin = {
        navController.navigate(DestinationRoutes.ROOT_LOGIN_SCREEN_ROUTE)
    }

    Scaffold(
        topBar = {
            TopAppBarWithBack(
                title = "App Settings",
                onBackClick = { navController.navigateUp() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoadingAuth) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (!uiState.isAuthenticated) {
            LoginPromptComponent(
                onLoginClick = onNavigateToLogin,
                modifier = Modifier.padding(paddingValues),
                title = "Please Login",
                subtitle = "You need to login to access app settings"
            )
        } else {
            AppSettingsContent(
                appSettings = uiState.appSettings,
                onThemeChanged = { viewModel.onTriggerEvent(SettingEvent.OnThemeChanged(it)) },
                onNotificationToggled = { viewModel.onTriggerEvent(SettingEvent.OnNotificationToggled(it)) },
                onPushNotificationToggled = { viewModel.onTriggerEvent(SettingEvent.OnPushNotificationToggled(it)) },
                onEmailNotificationToggled = { viewModel.onTriggerEvent(SettingEvent.OnEmailNotificationToggled(it)) },
                onBiometricAuthToggled = { viewModel.onTriggerEvent(SettingEvent.OnBiometricAuthToggled(it)) },
                onAutoSyncToggled = { viewModel.onTriggerEvent(SettingEvent.OnAutoSyncToggled(it)) },
                onLanguageChanged = { viewModel.onTriggerEvent(SettingEvent.OnLanguageChanged(it)) },
                onCurrencyChanged = { viewModel.onTriggerEvent(SettingEvent.OnCurrencyChanged(it)) },
                onLogout = { viewModel.onTriggerEvent(SettingEvent.OnLogoutClicked) },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun AppSettingsContent(
    appSettings: com.vidz.datastore.AppSettings,
    onThemeChanged: (ThemeMode) -> Unit,
    onNotificationToggled: (Boolean) -> Unit,
    onPushNotificationToggled: (Boolean) -> Unit,
    onEmailNotificationToggled: (Boolean) -> Unit,
    onBiometricAuthToggled: (Boolean) -> Unit,
    onAutoSyncToggled: (Boolean) -> Unit,
    onLanguageChanged: (Language) -> Unit,
    onCurrencyChanged: (Currency) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ModernSettingSection(title = "Appearance") {
                ModernDropdownSetting(
                    title = "Theme",
                    subtitle = "Choose your preferred theme",
                    currentValue = when (appSettings.themeMode) {
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                        ThemeMode.SYSTEM -> "System"
                    },
                    options = listOf("Light", "Dark", "System"),
                    onValueChanged = { value ->
                        val themeMode = when (value) {
                            "Light" -> ThemeMode.LIGHT
                            "Dark" -> ThemeMode.DARK
                            "System" -> ThemeMode.SYSTEM
                            else -> ThemeMode.SYSTEM
                        }
                        onThemeChanged(themeMode)
                    }
                )
            }
        }

        item {
            ModernSettingSection(title = "Notifications") {
                ModernSwitchSetting(
                    title = "Enable Notifications",
                    subtitle = "Receive app notifications",
                    checked = appSettings.notificationsEnabled,
                    onCheckedChange = onNotificationToggled
                )
                
                ModernSwitchSetting(
                    title = "Push Notifications",
                    subtitle = "Receive push notifications",
                    checked = appSettings.pushNotificationsEnabled,
                    onCheckedChange = onPushNotificationToggled,
                    enabled = appSettings.notificationsEnabled
                )
                
                ModernSwitchSetting(
                    title = "Email Notifications",
                    subtitle = "Receive email notifications",
                    checked = appSettings.emailNotificationsEnabled,
                    onCheckedChange = onEmailNotificationToggled,
                    enabled = appSettings.notificationsEnabled
                )
            }
        }

        item {
            ModernSettingSection(title = "Security") {
                ModernSwitchSetting(
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face unlock",
                    checked = appSettings.biometricAuthEnabled,
                    onCheckedChange = onBiometricAuthToggled
                )
            }
        }

        item {
            ModernSettingSection(title = "Data & Sync") {
                ModernSwitchSetting(
                    title = "Auto Sync",
                    subtitle = "Automatically sync data",
                    checked = appSettings.autoSyncEnabled,
                    onCheckedChange = onAutoSyncToggled
                )
            }
        }

        item {
            ModernSettingSection(title = "Preferences") {
                ModernDropdownSetting(
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
                
                ModernDropdownSetting(
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
            ModernSettingSection(title = "Account") {
                ModernLogoutButton(onLogout = onLogout)
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ModernSettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun ModernSwitchSetting(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
private fun ModernDropdownSetting(
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
            .padding(vertical = 12.dp)
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
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
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
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
private fun ModernLogoutButton(
    onLogout: () -> Unit
) {
    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            Icons.Default.ExitToApp,
            contentDescription = "Logout",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Sign Out",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
} 