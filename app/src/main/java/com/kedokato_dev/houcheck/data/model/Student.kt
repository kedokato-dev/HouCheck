package com.kedokato_dev.houcheck.data.model

import com.kedokato_dev.houcheck.database.entity.StudentEntity

data class Student(
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

data class StudentResponse(
    val success: Boolean,
    val data: Student
)


fun StudentResponse.toStudent(): Student {
    return Student(
        studentId = data.studentId,
        studentName = data.studentName,
        birthDate = data.birthDate,
        sex = data.sex,
        address = data.address,
        phone = data.phone,
        userPhone = data.userPhone,
        detailAddress = data.detailAddress,
        email = data.email
    )
}

fun StudentEntity.toStudent(): Student {
    return Student(
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
