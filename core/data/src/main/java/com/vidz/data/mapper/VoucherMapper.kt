package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.VoucherDto
import com.vidz.data.server.retrofit.dto.VoucherState as DtoVoucherState
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherState

class VoucherMapper(
    private val accountMapper: AccountMapper
) : BaseRemoteMapper<Voucher, VoucherDto> {

    override fun toDomain(external: VoucherDto): Voucher {
        return Voucher(
            voucherId = external.voucherId,
            orderId = external.orderId ?: 0L,
            account = accountMapper.toDomain(external.account),
            code = external.code,
            discountRate = external.discountRate,
            limitAmount = external.limitAmount,
            state = mapVoucherStateToDomain(external.state),
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            expiredAt = external.expiredAt
        )
    }

    override fun toRemote(domain: Voucher): VoucherDto {
        return VoucherDto(
            voucherId = domain.voucherId,
            orderId = domain.orderId.takeIf { it != 0L },
            account = accountMapper.toRemote(domain.account),
            code = domain.code,
            discountRate = domain.discountRate,
            limitAmount = domain.limitAmount,
            state = mapVoucherStateToDto(domain.state),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            expiredAt = domain.expiredAt
        )
    }

    private fun mapVoucherStateToDomain(dtoState: DtoVoucherState): VoucherState {
        return when (dtoState) {
            DtoVoucherState.USED -> VoucherState.Used
            DtoVoucherState.AVAILABLE -> VoucherState.Available
            DtoVoucherState.RESERVED -> VoucherState.Reserved
            DtoVoucherState.EXPIRED -> VoucherState.Expired
        }
    }

    private fun mapVoucherStateToDto(domainState: VoucherState): DtoVoucherState {
        return when (domainState) {
            VoucherState.Used -> DtoVoucherState.USED
            VoucherState.Available -> DtoVoucherState.AVAILABLE
            VoucherState.Reserved -> DtoVoucherState.RESERVED
            VoucherState.Expired -> DtoVoucherState.EXPIRED
        }
    }
} 