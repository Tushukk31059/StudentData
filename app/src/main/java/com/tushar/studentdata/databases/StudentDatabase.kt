package com.tushar.studentdata.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tushar.studentdata.dao.ArchiveDAO
import com.tushar.studentdata.dao.StudentDAO
import com.tushar.studentdata.entities.ArchiveEntity
import com.tushar.studentdata.entities.StudentEntity

@Database(entities = [StudentEntity::class,ArchiveEntity::class], version = 1, exportSchema = false)
abstract class StudentDatabase :RoomDatabase(){
    abstract fun studentDAO():StudentDAO
    abstract fun archiveDAO():ArchiveDAO


    companion object{

        @Volatile
        var INSTANCE:StudentDatabase?=null

    fun createDatabase(context: Context):StudentDatabase{
        if (INSTANCE==null){
            synchronized(this){
            INSTANCE= Room.databaseBuilder(context.applicationContext,
                StudentDatabase::class.java,
                "student_database"
            ).build()


            }

        }
        return INSTANCE!!
    }}
}