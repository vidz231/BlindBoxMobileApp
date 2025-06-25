package com.vidz.base.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralAppBar(
    modifier: Modifier = Modifier,
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onCartClick: () -> Unit = {},
    cartItemsCount: Int = 0,
    onChatClick: () -> Unit = {},
    isLargeAppBar: Boolean = false
) {
    //print
    Log.d("GeneralAppBar", "isLargeAppBar: $isLargeAppBar, cartItemsCount: $cartItemsCount")
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )

    if (isLargeAppBar) {
        LargeTopAppBar(
            title = {
                AppBarContent(
                    leadingContent = leadingContent,
                    onCartClick = onCartClick,
                    cartItemsCount = cartItemsCount,
                    onChatClick = onChatClick
                )
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            ),
            scrollBehavior = scrollBehavior,
            modifier = modifier
        )
    } else {
        TopAppBar(
            title = {
                AppBarContent(
                    leadingContent = leadingContent,
                    onCartClick = onCartClick,
                    cartItemsCount = cartItemsCount,
                    onChatClick = onChatClick
                )
            },
            colors = colors,
            scrollBehavior = scrollBehavior,
            modifier = modifier
        )
    }
}

@Composable
private fun AppBarContent(
    leadingContent: (@Composable RowScope.() -> Unit)?,
    onCartClick: () -> Unit,
    cartItemsCount: Int,
    onChatClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading content section
        Row(
            modifier = Modifier.weight(if (leadingContent != null) 1f else 1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent?.invoke(this)
        }
        
        // Action buttons section
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCartClick) {
                androidx.compose.material3.BadgedBox(
                    badge = {
                        if (cartItemsCount > 0) {
                            androidx.compose.material3.Badge {
                                androidx.compose.material3.Text(
                                    text = cartItemsCount.coerceAtMost(99).toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            IconButton(onClick = onChatClick) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat", 
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
} 