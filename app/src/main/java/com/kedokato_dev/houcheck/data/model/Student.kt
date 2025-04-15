package com.kedokato_dev.houcheck.data.model

data class StudentResponse(
    val success: Boolean,
    val data: Student
)

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