package com.tushar.studentdata.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tushar.studentdata.repository.StudentRepository

class VMFactory(private val repo:StudentRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentViewModel(repo) as T
    }
}