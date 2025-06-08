package com.vidz.order.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.order.model.OrderItem
import com.vidz.order.model.OrderStatus
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    modifier: Modifier = Modifier,
    orderDetailViewModel: OrderDetailViewModel = hiltViewModel(),
    onShowSnackbar: ((String) -> Unit)? = null
) {
    val uiState = orderDetailViewModel.uiState.collectAsStateWithLifecycle()

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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            OrderInfoSection(order = uiState.value.order!!)
                        }
                        
                        item {
                            OrderItemsSection(order = uiState.value.order!!)
                        }
                        
                        item {
                            OrderSummarySection(order = uiState.value.order!!)
                        }
                        
                        if (uiState.value.order!!.status == OrderStatus.PENDING) {
                            item {
                                CancelOrderButton(
                                    onCancelClick = {
                                        orderDetailViewModel.cancelOrder()
                                        onShowSnackbar?.invoke("Đơn hàng đã được hủy")
                                        navController.navigateUp()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderInfoSection(order: OrderItem) {
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow("Mã đơn hàng", order.orderNumber)
            InfoRow("Ngày đặt", order.date)
            InfoRow("Trạng thái", getStatusText(order.status))
            order.shippingAddress?.let { InfoRow("Địa chỉ giao hàng", it) }
            order.paymentMethod?.let { InfoRow("Phương thức thanh toán", it) }
            order.estimatedDeliveryDate?.let { InfoRow("Ngày giao hàng dự kiến", it) }
            order.note?.let { InfoRow("Ghi chú", it) }
        }
    }
}

@Composable
private fun OrderItemsSection(order: OrderItem) {
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            order.items.forEach { item ->
                OrderItemRow(item = item)
                if (item != order.items.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderSummarySection(order: OrderItem) {
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
                text = "Tổng cộng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tổng tiền",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatCurrency(order.totalAmount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderProductItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Số lượng: ${item.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formatCurrency(item.price * item.quantity),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
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
private fun CancelOrderButton(onCancelClick: () -> Unit) {
    Button(
        onClick = onCancelClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Hủy đơn hàng")
    }
}

private fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Chờ xử lý"
        OrderStatus.PROCESSING -> "Đang xử lý"
        OrderStatus.COMPLETED -> "Hoàn thành"
        OrderStatus.CANCELLED -> "Đã hủy"
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
} 