package com.kedokato_dev.houcheck.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Thêm dòng này để làm khóa chính
    val fullName: String?,
    val studentId: String?,
    val birthDate: String?,
    val sex: String?,
    val address: String?,
    val phoneUser: String?,
    val phoneParent: String?,
    val email: String?
)
