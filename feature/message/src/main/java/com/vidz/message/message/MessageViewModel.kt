package com.vidz.message.message

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.model.Conversation
import com.vidz.domain.model.Message
import com.vidz.domain.usecase.ConnectWebSocketUseCase
import com.vidz.domain.usecase.FindOrCreateConversationUseCase
import com.vidz.domain.usecase.GetCurrentUserTokenUseCase
import com.vidz.domain.usecase.GetCurrentUserUseCase
import com.vidz.domain.usecase.ObserveMessagesUseCase
import com.vidz.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val findOrCreateConversationUseCase: FindOrCreateConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val connectWebSocketUseCase: ConnectWebSocketUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCurrentUserTokenUseCase: GetCurrentUserTokenUseCase

) : BaseViewModel<MessageViewModel.MessageViewEvent,
        MessageViewModel.MessageViewState,
        MessageViewModel.MessageViewModelState>(
    initState = MessageViewModelState()
) {

    init {
        initializeChat()
    }

    private fun initializeChat() {
        viewModelScope.launch {
            // Get current user first
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is Success -> {
                        val currentUser = result.data
                        viewModelState.update { it.copy(currentUser = currentUser) }
                        
                        // Find or create conversation
                        findOrCreateConversation(currentUser.accountId)

                        // Get token and connect WebSocket
                        getCurrentUserTokenUseCase().collect { tokenResult ->
                            when(tokenResult){
                                is Success ->{
                                    val currentUserToken = tokenResult.data
                                    connectWebSocket(currentUser.email, currentUserToken)
                                }
                                else -> {
                                    Log.e("MessageViewModel", "Failed to get current user token")
                                }
                            }
                        }
                    }
                    else -> {
                        Log.e("MessageViewModel", "Failed to get current user")
                    }
                }
            }
        }
    }

    private fun findOrCreateConversation(userId: Long) {
        viewModelScope.launch {
            findOrCreateConversationUseCase(userId).collect { result ->
                when (result) {
                    is Success -> {
                        val conversation = result.data
                        viewModelState.update { 
                            it.copy(
                                conversation = conversation,
                                isLoading = false
                            ) 
                        }
                        observeMessages(conversation.conversationId)
                    }
                    is ServerError -> {
                        viewModelState.update { 
                            it.copy(
                                error = "Failed to load conversation",
                                isLoading = false
                            ) 
                        }
                    }
                    Init -> {
                        viewModelState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun connectWebSocket(userEmail: String, token: String) {
        viewModelScope.launch {
            connectWebSocketUseCase(userEmail, token).collect { result ->
                when (result) {
                    is Success -> {
                        viewModelState.update { it.copy(isWebSocketConnected = true) }
                        Log.d("MessageViewModel", "WebSocket connected successfully")
                        
                        // Auto-subscription to messages is now handled in StompWebSocketClient
                        // after receiving the CONNECTED frame
                        subscribeToWebSocketMessages(userEmail)
                    }
                    is ServerError -> {
                        Log.e("MessageViewModel", "WebSocket connection failed: ${result.message}")
                        viewModelState.update { 
                            it.copy(
                                error = "Connection failed: ${result.message}",
                                isWebSocketConnected = false
                            ) 
                        }
                    }
                    else -> { /* Handle other states */ }
                }
            }
        }
    }

    private fun subscribeToWebSocketMessages(userEmail: String) {
        viewModelScope.launch {
            // Subscribe to real-time WebSocket messages
            observeMessagesUseCase.subscribeToMessages(userEmail).collect { message ->
                Log.d("MessageViewModel", "Received WebSocket message: ${message.content}")
                
                // Update the message with current user context
                val currentUserId = viewModelState.value.currentUser?.accountId
                val messageWithUserFlag = message.copy(
                    isFromCurrentUser = message.sender.accountId == currentUserId
                )
                
                // This will be handled by the observeMessages flow, but we can log for debugging
                Log.d("MessageViewModel", "WebSocket message processed for user $currentUserId")
            }
        }
    }

    private fun observeMessages(conversationId: Long) {
        viewModelScope.launch {
            observeMessagesUseCase(conversationId).collect { messages ->
                Log.d("MessageViewModel", "observeMessages: Received ${messages.size} messages for conversation $conversationId")
                
                val currentUserId = viewModelState.value.currentUser?.accountId
                Log.d("MessageViewModel", "Current user ID: $currentUserId")
                
                val messagesWithCurrentUserFlag = messages.map { message ->
                    val isFromCurrentUser = message.sender.accountId == currentUserId
                    Log.d("MessageViewModel", "Message ${message.messageId}: sender=${message.sender.accountId}, isFromCurrentUser=$isFromCurrentUser")
                    message.copy(isFromCurrentUser = isFromCurrentUser)
                }
                
                viewModelState.update { 
                    it.copy(messages = messagesWithCurrentUserFlag) 
                }
                Log.d("MessageViewModel", "Updated ViewModel state with ${messagesWithCurrentUserFlag.size} messages")
            }
        }
    }

    private fun sendMessage(content: String) {
        val currentState = viewModelState.value
        val conversation = currentState.conversation ?: return
        
        if (content.isBlank()) return

        viewModelScope.launch {
            viewModelState.update { it.copy(isSendingMessage = true) }
            
            sendMessageUseCase(conversation.conversationId, content).collect { result ->
                when (result) {
                    is Success -> {
                        // Clear input field
                        viewModelState.update { 
                            it.copy(
                                messageInput = "",
                                isSendingMessage = false
                            ) 
                        }
                        Log.d("MessageViewModel", "Message sent successfully")
                    }
                    is ServerError -> {
                        viewModelState.update { 
                            it.copy(
                                error = "Failed to send message",
                                isSendingMessage = false
                            ) 
                        }
                    }
                    else -> { /* Handle other states */ }
                }
            }
        }
    }

    override fun onTriggerEvent(event: MessageViewEvent) {
        when (event) {
            is MessageViewEvent.UpdateMessageInput -> {
                viewModelState.update { it.copy(messageInput = event.text) }
            }
            is MessageViewEvent.SendMessage -> {
                sendMessage(viewModelState.value.messageInput)
            }
            MessageViewEvent.ClearError -> {
                viewModelState.update { it.copy(error = null) }
            }
        }
    }

    data class MessageViewModelState(
        val isLoading: Boolean = false,
        val isSendingMessage: Boolean = false,
        val error: String? = null,
        val currentUser: com.vidz.domain.model.Account? = null,
        val conversation: Conversation? = null,
        val messages: List<Message> = emptyList(),
        val messageInput: String = "",
        val isWebSocketConnected: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState = MessageViewState(
            isLoading = isLoading,
            isSendingMessage = isSendingMessage,
            error = error,
            conversation = conversation,
            messages = messages,
            messageInput = messageInput,
            isWebSocketConnected = isWebSocketConnected
        )
    }

    data class MessageViewState(
        val isLoading: Boolean,
        val isSendingMessage: Boolean,
        val error: String?,
        val conversation: Conversation?,
        val messages: List<Message>,
        val messageInput: String,
        val isWebSocketConnected: Boolean
    ) : ViewState()

    sealed class MessageViewEvent : ViewEvent {
        data class UpdateMessageInput(val text: String) : MessageViewEvent()
        data object SendMessage : MessageViewEvent()
        data object ClearError : MessageViewEvent()
    }
} 