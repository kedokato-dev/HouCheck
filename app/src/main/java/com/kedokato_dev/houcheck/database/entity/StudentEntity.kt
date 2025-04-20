package com.kedokato_dev.houcheck.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "fullName")
    val fullName: String,
    @ColumnInfo(name = "studentId")
    val studentId: String,
    @ColumnInfo(name = "birthDate")
    val birthDate: String,
    @ColumnInfo(name = "sex")
    val sex: String,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "phoneUser")
    val phoneUser: String,
    @ColumnInfo(name = "phoneParent")
    val phoneParent: String,
    @ColumnInfo(name = "email")
    val email: String
)
