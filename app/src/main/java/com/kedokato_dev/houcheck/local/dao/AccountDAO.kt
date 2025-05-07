package com.kedokato_dev.houcheck.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kedokato_dev.houcheck.local.entity.AccountEntity

@Dao
interface AccountDAO {
    @Insert
    suspend fun insertAccount(account: AccountEntity)

    @Query("SELECT * FROM account")
    suspend fun getAllAccounts(): AccountEntity

    @Query("DELETE FROM account")
    suspend fun deleteAllAccounts()

    @Query("SELECT * FROM account WHERE userName = :userName")
    suspend fun getAccountByUserName(userName: String): AccountEntity?

    @Update
    suspend fun updateAccount(account: AccountEntity)
}