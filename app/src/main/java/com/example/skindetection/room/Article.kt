package com.example.skindetection.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val title: String,
    val author: String,
    val content: String,
    val imageUrl: String
)
