package com.kedokato_dev.houcheck.repository

import com.kedokato_dev.houcheck.local.dao.AccountDAO
import com.kedokato_dev.houcheck.local.entity.AccountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AccountRepository(
    private val accountDao: AccountDAO
) {

    suspend fun insertAccount(account: AccountEntity) = withContext(Dispatchers.IO) {
        accountDao.insertAccount(account)
    }

    suspend fun checkAccountExist(userName: String): Boolean = withContext(Dispatchers.IO) {
        val existingAccount = accountDao.getAccountByUserName(userName)
        return@withContext existingAccount != null
    }

    suspend fun getAccount(): Result<AccountEntity> = withContext(Dispatchers.IO) {
        try {
            val account = accountDao.getAllAccounts()
            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllAccounts() = withContext(Dispatchers.IO) {
        accountDao.deleteAllAccounts()
    }

    suspend fun updateAccount(account: AccountEntity) = withContext(Dispatchers.IO) {
        accountDao.updateAccount(account)
    }
}