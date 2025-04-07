package com.tushar.studentdata.repository

import com.tushar.studentdata.dao.ArchiveDAO
import com.tushar.studentdata.dao.StudentDAO
import com.tushar.studentdata.entities.ArchiveEntity
import com.tushar.studentdata.entities.StudentEntity

class StudentRepository(
    private val studentDao: StudentDAO,
    private val archiveDao: ArchiveDAO
) {
    val students = studentDao.getStudent()
    val archives = archiveDao.getArchive()

    suspend fun insertStudent(studentObj:StudentEntity) {
        studentDao.insertStudent(studentObj)
    }
    suspend fun deleteStudent(id: Int) {
        studentDao.deleteStudentById(id)
    }

    suspend fun insertArchive(studentObj: StudentEntity) {
        studentDao.deleteStudentById(studentObj.id)
        archiveDao.insertArchive(ArchiveEntity(studentName = studentObj.studentName, rollNo = studentObj.rollNo.toString()))
    }

    suspend fun deleteArchive(id: Int) {
        archiveDao.deleteArchiveById(id)
    }
}