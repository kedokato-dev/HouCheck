package com.kedokato_dev.houcheck.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kedokato_dev.houcheck.network.model.Student


@Entity(tableName = "profile")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: String,
    val studentName: String,
    val birthDate: String,
    val sex: String,
    val address: String,
    val phone: String,
    val userPhone: String,
    val detailAddress: String,
    val email: String
)



fun Student.toEntity(): StudentEntity {
    return StudentEntity(
        studentId = this.studentId,
        studentName = this.studentName,
        birthDate = this.birthDate,
        sex = this.sex,
        address = this.address,
        phone = this.phone,
        userPhone = this.userPhone,
        detailAddress = this.detailAddress,
        email = this.email
    )
}

