package com.example.skindetection.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdb.room.AdminDao
import com.example.roomdb.room.ArticleDao
import com.example.skindetection.room.Disease
import com.example.roomdb.room.DiseaseDao

@Database(
    entities = [Article::class, Disease::class, Admin::class],
    version = 4
)
abstract class AppDB : RoomDatabase() {

    abstract fun articleDao(): ArticleDao
    abstract fun diseaseDao(): DiseaseDao
    abstract fun adminDao(): AdminDao

    companion object {
        @Volatile private var instance: AppDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDB::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration() // Tambahkan ini jika ingin menghapus dan membuat ulang database pada perubahan versi
            .build()
    }
}