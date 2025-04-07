package com.tushar.studentdata.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tushar.studentdata.databases.StudentDatabase
import androidx.lifecycle.viewModelScope
import com.tushar.studentdata.entities.ArchiveEntity
import com.tushar.studentdata.entities.StudentEntity
import com.tushar.studentdata.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(private val repo:StudentRepository):ViewModel() {


    val students=repo.students
    val archive=repo.archives
    fun insertStudent(obj:StudentEntity){
        viewModelScope.launch{
            repo.insertStudent(obj)
        }
    }
        fun delStudent(id:Int){
            viewModelScope.launch {
                repo.deleteStudent(id)
            }}

        fun insertArchive(obj:StudentEntity){
            viewModelScope.launch {
                repo.insertArchive(obj)

            }
        }


    fun delArchive(id:Int){
        viewModelScope.launch {
            repo.deleteArchive(id)
        }
    }
}