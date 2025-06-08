package com.vidz.order.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.order.OrderStatus
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    modifier: Modifier = Modifier,
    orderDetailViewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState = orderDetailViewModel.uiState.collectAsStateWithLifecycle()
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        orderDetailViewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { orderDetailViewModel.onTriggerEvent(OrderDetailViewModel.OrderDetailViewEvent.RefreshOrder) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.value.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.value.error != null -> {
                    Text(
                        text = uiState.value.error ?: "Có lỗi xảy ra",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.value.order != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Order Status Card
                        OrderStatusCard(
                            order = uiState.value.order!!,
                            onCancelClick = { showCancelDialog = true }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Order Info Card
                        OrderInfoCard(order = uiState.value.order!!)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Items Card
                        OrderItemsCard(order = uiState.value.order!!)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Payment Card
                        PaymentCard(order = uiState.value.order!!)
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Hủy đơn hàng") },
            text = { Text("Bạn có chắc chắn muốn hủy đơn hàng này?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        orderDetailViewModel.onTriggerEvent(OrderDetailViewModel.OrderDetailViewEvent.CancelOrder)
                        showCancelDialog = false
                    }
                ) {
                    Text("Hủy đơn hàng", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    // Show snackbar when order is cancelled
    LaunchedEffect(uiState.value.isCancelled) {
        if (uiState.value.isCancelled) {
            // TODO: Show snackbar
        }
    }
}

@Composable
fun OrderStatusCard(
    order: OrderItem,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
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
                    text = "Trạng thái đơn hàng",
                    style = MaterialTheme.typography.titleMedium
                )
                StatusChip(status = order.status)
            }
            
            if (order.status == OrderStatus.PENDING) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hủy đơn hàng")
                }
            }
        }
    }
}

@Composable
fun OrderInfoCard(order: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Thông tin đơn hàng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InfoRow("Mã đơn hàng", order.orderNumber)
            InfoRow("Ngày đặt", order.date)
            InfoRow("Địa chỉ giao hàng", order.shippingAddress)
            order.estimatedDeliveryDate?.let {
                InfoRow("Dự kiến giao hàng", it)
            }
            order.note?.let {
                InfoRow("Ghi chú", it)
            }
        }
    }
}

@Composable
fun OrderItemsCard(order: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Sản phẩm",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Số lượng: ${item.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = formatCurrency(item.price * item.quantity),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tổng cộng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatCurrency(order.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PaymentCard(order: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Thông tin thanh toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InfoRow("Phương thức thanh toán", order.paymentMethod)
            InfoRow("Tổng tiền", formatCurrency(order.totalAmount))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun StatusChip(status: OrderStatus) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        OrderStatus.PROCESSING -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }

    Surface(
        modifier = Modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = when (status) {
                OrderStatus.PENDING -> "Chờ xử lý"
                OrderStatus.PROCESSING -> "Đang xử lý"
                OrderStatus.COMPLETED -> "Hoàn thành"
                OrderStatus.CANCELLED -> "Đã hủy"
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
} 