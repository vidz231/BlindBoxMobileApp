@file:OptIn(ExperimentalMaterial3Api::class)

package com.vidz.base.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun FloatingNotificationSetupDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var notificationStatus by remember { mutableStateOf<NotificationBannerManager.NotificationStatus?>(null) }
    
    LaunchedEffect(showDialog) {
        if (showDialog) {
            notificationStatus = NotificationBannerManager.getNotificationStatus(context)
        }
    }
    
    if (showDialog && notificationStatus != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "🚀 Enable Banner Notifications",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Get instant banner notifications that appear on top of other apps for cart reminders and order updates! Shopee-style for the best shopping experience! 🛒✨",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Notification Status Items
                    NotificationStatusItem(
                        title = "📱 Basic Notifications",
                        description = "Allow app to send notifications",
                        isEnabled = notificationStatus!!.notificationsEnabled,
                        onAction = { 
                            NotificationBannerManager.openNotificationSettings(context)
                        }
                    )
                    
                    NotificationStatusItem(
                        title = "🔔 Banner Notifications",
                        description = "Show notifications as floating banners",
                        isEnabled = notificationStatus!!.headsUpEnabled,
                        onAction = { 
                            NotificationBannerManager.openNotificationSettings(context)
                        }
                    )
                    
                    if (notificationStatus!!.doNotDisturbEnabled) {
                        NotificationStatusItem(
                            title = "🔕 Do Not Disturb",
                            description = "Currently blocking banner notifications",
                            isEnabled = false,
                            onAction = { 
                                NotificationBannerManager.openNotificationSettings(context)
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (notificationStatus!!.bannerNotificationsWork) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🎉 Banner notifications are ready!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Button(
                                    onClick = {
                                        // Test banner notification with Shopee style
                                        NotificationBannerManager.showTestBannerNotification(
                                            context = context,
                                            iconResId = android.R.drawable.ic_dialog_info
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("🧪 Test Banner Notification")
                                }
                            }
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "⚠️ Banner notifications need setup. Tap 'Fix' buttons above to enable them.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationStatusItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                    }
                )
            }
            
            if (!isEnabled) {
                OutlinedButton(
                    onClick = onAction
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fix")
                }
            } else {
                Text(
                    text = "✅",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
} 