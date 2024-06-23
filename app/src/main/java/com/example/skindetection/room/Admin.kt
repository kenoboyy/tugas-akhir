package com.example.skindetection.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Admin (
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val username: String,
    val password: String
)
