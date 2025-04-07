package com.tushar.studentdata.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tushar.studentdata.entities.ArchiveEntity
import com.tushar.studentdata.entities.StudentEntity

@Dao
interface StudentDAO {
    @Insert
    suspend fun insertStudent(obj:StudentEntity)
    @Query("DELETE FROM student_table WHERE id=:id")
    suspend fun deleteStudentById(id:Int)
    @Query("SELECT * FROM STUDENT_TABLE")
    fun getStudent():LiveData<List<StudentEntity>>
}

@Dao
interface ArchiveDAO {
    @Insert
    suspend fun insertArchive(obj: ArchiveEntity)
    @Query("DELETE FROM archive_table where id=:id")
    suspend fun deleteArchiveById(id:Int)
    @Query("SELECT * FROM archive_table")
    fun getArchive():LiveData<List<ArchiveEntity>>
}