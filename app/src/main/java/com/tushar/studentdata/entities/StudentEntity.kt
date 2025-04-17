package com.tushar.studentdata.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_table")
data class StudentEntity(
   @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val studentName: String,
    val rollNo: Int,
    val img:ByteArray?
)
