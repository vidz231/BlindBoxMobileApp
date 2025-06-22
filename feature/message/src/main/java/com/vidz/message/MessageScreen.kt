package com.vidz.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.domain.model.Message
import com.vidz.message.message.MessageViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    messageViewModel: MessageViewModel = hiltViewModel()
) {
    val messageUiState = messageViewModel.uiState.collectAsStateWithLifecycle()
    
    MessageScreen(
        navController = navController,
        messageUiState = messageUiState,
        onEvent = messageViewModel::onTriggerEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavController,
    messageUiState: State<MessageViewModel.MessageViewState>,
    onEvent: (MessageViewModel.MessageViewEvent) -> Unit
) {
    //region Define Var
    val listState = rememberLazyListState()
    val state = messageUiState.value
    //endregion

    //region Event Handler
    val onBackPressed: () -> Unit = {
        navController.navigateUp()
    }
    
    val onSendMessage: () -> Unit = {
        onEvent(MessageViewModel.MessageViewEvent.SendMessage)
    }
    
    val onMessageInputChanged: (String) -> Unit = { text ->
        onEvent(MessageViewModel.MessageViewEvent.UpdateMessageInput(text))
    }
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(state.messages) {
        if (state.messages.isNotEmpty()) {
            try {
                listState.animateScrollToItem(state.messages.size - 1)
            } catch (e: Exception) {
                // Fallback to scroll to item without animation
                listState.scrollToItem(state.messages.size - 1)
            }
        }
    }
    //endregion

    //region ui
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Customer Support",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.conversation?.staff?.firstName?.isNotEmpty() == true) {
                            Text(
                                "Chat with ${state.conversation.staff.firstName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            MessageInputBar(
                messageInput = state.messageInput,
                onMessageInputChanged = onMessageInputChanged,
                onSendMessage = onSendMessage,
                isSending = state.isSendingMessage,
                enabled = state.isWebSocketConnected && !state.isLoading
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onEvent(MessageViewModel.MessageViewEvent.ClearError) }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.messages.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "👋 Welcome to Customer Support",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Start a conversation with our support team",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        items(state.messages) { message ->
                            MessageBubble(message = message)
                        }
                    }
                }
            }
        }
    }
    
    //region Dialog and Sheet
    //endregion
    //endregion
}

@Composable
private fun MessageInputBar(
    messageInput: String,
    onMessageInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    isSending: Boolean,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = onMessageInputChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your message...") },
                enabled = enabled,
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FloatingActionButton(
                onClick = onSendMessage,
                modifier = Modifier.size(48.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: Message) {
    val isFromCurrentUser = message.isFromCurrentUser
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isFromCurrentUser) {
            // Staff message
            Column(
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.sender.firstName.ifEmpty { "Support" },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (message.createdAt.isNotEmpty()) {
                    Text(
                        text = formatMessageTime(message.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }
            }
        } else {
            // Current user message
            Column(
                modifier = Modifier.widthIn(max = 280.dp),
                horizontalAlignment = Alignment.End
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 4.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                if (message.createdAt.isNotEmpty()) {
                    Text(
                        text = formatMessageTime(message.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 12.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun formatMessageTime(dateTime: String): String {
    return try {
        // Parse ISO 8601 format: "2025-06-21T22:54:38.600009+07:00"
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormatter.parse(dateTime)
        date?.let { outputFormatter.format(it) } ?: "Now"
    } catch (e: Exception) {
        try {
            // Fallback: try without microseconds
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val outputFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormatter.parse(dateTime)
            date?.let { outputFormatter.format(it) } ?: "Now"
        } catch (e2: Exception) {
            // Last fallback: just show part of the time string
            if (dateTime.length >= 16) {
                dateTime.substring(11, 16) // Extract HH:mm from ISO string
            } else {
                "Now"
            }
        }
    }
}