package com.kedokato_dev.houcheck.data.model


data class StudentName(
    val name: String,
    val studentId: String
)

data class StudentNameResponse(
    val success: Boolean,
    val data: StudentName
)

fun StudentNameResponse.toStudentName(): StudentName {
    return StudentName(
        name = this.data.name,
        studentId = this.data.studentId
    )
}