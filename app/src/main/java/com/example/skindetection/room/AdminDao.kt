package com.example.roomdb.room

import androidx.room.*
import com.example.skindetection.room.Admin
import com.example.skindetection.room.Article

@Dao
interface AdminDao {

    @Query("SELECT * FROM admin WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): Admin?

    @Query("SELECT * FROM admin WHERE username = :username")
    suspend fun getAdminByUsername(username: String): Admin?

    @Insert
    suspend fun insertAdmin(admin: Admin)
}