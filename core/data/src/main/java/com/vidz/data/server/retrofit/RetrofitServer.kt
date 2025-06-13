package com.vidz.data.server.retrofit

import com.vidz.data.server.retrofit.api.* // Import all API interfaces
import com.vidz.data.server.retrofit.dto.* // Import all DTOs
import com.vidz.domain.repository.BlindBoxRepository
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java

@Singleton
class RetrofitServer @Inject constructor(private val retrofit: Retrofit) {
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val brandApi: BrandApi by lazy { retrofit.create(BrandApi::class.java) }
    val orderApi: OrderApi by lazy { retrofit.create(OrderApi::class.java) }
    val skuApi: SkuApi by lazy { retrofit.create(SkuApi::class.java) }
    val blindBoxApi: BlindBoxApi by lazy { retrofit.create(BlindBoxApi::class.java) }
    val transactionApi: TransactionApi by lazy { retrofit.create(TransactionApi::class.java) }
    val messageApi: MessageApi by lazy { retrofit.create(MessageApi::class.java) }
    val shippingInfoApi: ShippingInfoApi by lazy { retrofit.create(ShippingInfoApi::class.java) }
}
