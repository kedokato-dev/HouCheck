package com.kedokato_dev.houcheck.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userName: String,
    val passWord: String,
)
