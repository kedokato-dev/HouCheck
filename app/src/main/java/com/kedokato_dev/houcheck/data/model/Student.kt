package com.kedokato_dev.houcheck.data.model

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
    val data: StudentData
)

data class StudentData(
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