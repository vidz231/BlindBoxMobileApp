package com.vidz.setting

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vidz.base.components.LoginPromptComponent
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.domain.model.Account
import com.vidz.domain.model.Transaction
import com.vidz.domain.model.TransactionStatus
import com.vidz.domain.model.TransactionType
import com.vidz.setting.setting.SettingEvent
import com.vidz.setting.setting.SettingUiState
import com.vidz.setting.setting.SettingViewModel
import com.vidz.setting.setting.TransactionFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Keep original MaterialTheme colors - no custom color overrides

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        Log.d("SettingScreen", "Screen attached, refreshing auth state")
        viewModel.onTriggerEvent(SettingEvent.OnRefreshAuth)
        onDispose {
            Log.d("SettingScreen", "Screen detached")
        }
    }

    val onNavigateToLogin = {
        navController.navigate(DestinationRoutes.ROOT_LOGIN_SCREEN_ROUTE)
    }

    val onNavigateToEditProfile = {
        navController.navigate(DestinationRoutes.ROOT_EDIT_PROFILE_SCREEN_ROUTE)
    }

    val onNavigateToAppSettings = {
        navController.navigate(DestinationRoutes.APP_SETTINGS_SCREEN_ROUTE)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            topBar = {
                IOSTopAppBar(
                    onAppSettingsClick = onNavigateToAppSettings
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if(uiState.isLoadingAuth) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else if (!uiState.isAuthenticated) {
                LoginPromptComponent(
                    onLoginClick = onNavigateToLogin,
                    modifier = Modifier.padding(paddingValues),
                    title = "Please Login",
                    subtitle = "You need to login to access your profile and transactions"
                )
            } else if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                IOSSettingContent(
                    uiState = uiState,
                    onEditProfile = onNavigateToEditProfile,
                    onFilterChanged = { viewModel.onTriggerEvent(SettingEvent.OnTransactionFilterChanged(it)) },
                    onRefreshTransactions = { viewModel.onTriggerEvent(SettingEvent.OnRefreshTransactions) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IOSTopAppBar(
    onAppSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "Profile",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        actions = {
            IconButton(onClick = onAppSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun IOSSettingContent(
    uiState: SettingUiState,
    onEditProfile: () -> Unit,
    onFilterChanged: (TransactionFilter) -> Unit,
    onRefreshTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            IOSUserProfileSection(
                user = uiState.userProfile,
                onEditProfile = onEditProfile
            )
        }

        item {
            IOSTransactionsSection(
                transactions = uiState.transactions,
                selectedFilter = uiState.transactionFilter,
                isLoading = uiState.isLoadingTransactions,
                onFilterChanged = onFilterChanged,
                onRefresh = onRefreshTransactions
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun IOSUserProfileSection(
    user: Account?,
    onEditProfile: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (user != null) {
                        Text(
                            text = "${user.firstName} ${user.lastName}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = user.email,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Loading...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // iOS-style profile image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Balance Card - iOS style
            if (user != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Account Balance",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${String.format("%.2f", user.balance)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (user.isVerified) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified Account",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Button - iOS style
            Surface(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Profile",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun IOSTransactionsSection(
    transactions: List<Transaction>,
    selectedFilter: TransactionFilter,
    isLoading: Boolean,
    onFilterChanged: (TransactionFilter) -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transactions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // iOS-style segmented control (filter chips)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(TransactionFilter.entries) { filter ->
                        Surface(
                            onClick = { onFilterChanged(filter) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedFilter == filter) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shadowElevation = if (selectedFilter == filter) 2.dp else 0.dp
                        ) {
                            Text(
                                text = filter.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFilter == filter) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                IOSTransactionList(transactions = transactions)
            }
        }
    }
}

@Composable
private fun IOSTransactionList(transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Your transaction history will appear here",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.height(400.dp)
        ) {
            items(transactions) { transaction ->
                IOSTransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
private fun IOSTransactionItem(transaction: Transaction) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when (transaction.type) {
                            TransactionType.Deposit -> "Deposit"
                            TransactionType.Order -> "Order #${transaction.orderId}"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = formatDate(transaction.createdAt),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when (transaction.status) {
                            TransactionStatus.Success -> "Completed"
                            TransactionStatus.Pending -> "Pending"
                            TransactionStatus.Failed -> "Failed"
                        },
                        fontSize = 12.sp,
                        color = when (transaction.status) {
                            TransactionStatus.Success -> MaterialTheme.colorScheme.primary
                            TransactionStatus.Pending -> MaterialTheme.colorScheme.tertiary
                            TransactionStatus.Failed -> MaterialTheme.colorScheme.error
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${if (transaction.amount >= 0) "+" else ""}${String.format("%.2f", transaction.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        transaction.amount > 0 -> MaterialTheme.colorScheme.primary
                        transaction.amount < 0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // iOS-style separator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 16.dp)
            )
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}