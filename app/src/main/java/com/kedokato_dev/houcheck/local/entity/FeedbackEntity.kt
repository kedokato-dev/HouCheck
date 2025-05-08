package com.kedokato_dev.houcheck.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val message: String,
    val createdAt: String
)