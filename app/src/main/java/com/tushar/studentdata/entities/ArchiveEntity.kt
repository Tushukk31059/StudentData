package com.tushar.studentdata.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "archive_table")
data class ArchiveEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val studentName : String,
    val rollNo : String
)