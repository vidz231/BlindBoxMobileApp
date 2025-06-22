package com.vidz.data.websocket

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketConfig @Inject constructor() {
    
    // TODO: Move these to BuildConfig or shared preferences for different environments
    val baseUrl: String = "ws://52.163.66.235:8080"
    val wsEndpoint: String = "/8lind8ox-ws"
    val messageSubscriptionTopic: String = "message"
    val sendMessageDestination: String = "/app/chat.sendMessage"
    
    fun getWebSocketUrl(): String = "$baseUrl$wsEndpoint"
    
    fun getMessageSubscriptionDestination(userEmail: String): String = 
        "$messageSubscriptionTopic/$userEmail"
    
    fun getSendMessageDestination(conversationId: Long): String = 
        "$sendMessageDestination/$conversationId"
}