package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.AccountDto
import com.vidz.data.server.retrofit.dto.RoleEnum
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountRole
import com.vidz.domain.model.ShippingInfo
import java.math.BigDecimal

class AccountMapper(
    private val shippingInfoMapper: ShippingInfoMapper
) : BaseRemoteMapper<Account, AccountDto> {

    override fun toDomain(external: AccountDto): Account {
        return Account(
            accountId = external.accountId,
            email = external.email,
            firstName = external.firstName,
            lastName = external.lastName,
            password = external.password,
            avatarUrl = external.avatarUrl,
            balance = external.balance.toFloat().toDouble(),
            updateBalanceAt = external.updateBalanceAt,
            role = mapAccountRoleToDomain(external.role),
            isVerified = external.isVerified,
            verifiedAt = external.verifiedAt,
            isVisible = external.isVisible,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            defaultShippingInfo = external.defaultShippingInfo?.let { 
                shippingInfoMapper.toDomain(it) 
            } ?: ShippingInfo()
        )
    }

    override fun toRemote(domain: Account): AccountDto {
        return AccountDto(
            accountId = domain.accountId,
            email = domain.email,
            firstName = domain.firstName,
            lastName = domain.lastName,
            password = domain.password.takeIf { it.isNotEmpty() }
                    .toString(),
            avatarUrl = domain.avatarUrl.takeIf { it.isNotEmpty() }
                    .toString(),
            balance = BigDecimal.valueOf(domain.balance),
            updateBalanceAt = domain.updateBalanceAt.takeIf { it.isNotEmpty() }
                    .toString(),
            role = mapAccountRoleToDto(domain.role),
            isVerified = domain.isVerified,
            verifiedAt = domain.verifiedAt.takeIf { it.isNotEmpty() }
                    .toString(),
            isVisible = domain.isVisible,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            defaultShippingInfo = shippingInfoMapper.toRemote(domain.defaultShippingInfo)
        )
    }

    private fun mapAccountRoleToDomain(dtoRole: RoleEnum): AccountRole {
        return when (dtoRole) {
            RoleEnum.ADMIN -> AccountRole.Admin
            RoleEnum.USER -> AccountRole.Staff
        }
    }

    private fun mapAccountRoleToDto(domainRole: AccountRole): RoleEnum {
        return when (domainRole) {
            AccountRole.Admin -> RoleEnum.ADMIN
            AccountRole.Staff -> RoleEnum.USER
            AccountRole.Customer -> RoleEnum.USER
        }
    }
} 