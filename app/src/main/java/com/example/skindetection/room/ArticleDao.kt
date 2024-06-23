package com.example.roomdb.room

import androidx.room.*
import com.example.skindetection.room.Article

@Dao
interface ArticleDao {
    @Insert
    suspend fun addArticle(article: Article)

    @Query("SELECT * FROM article ORDER BY id DESC")
    suspend fun getArticles() : List<Article>

    @Query("SELECT * FROM article WHERE id=:article_id")
    suspend fun getArticle(article_id: Int) : List<Article>

    @Update
    suspend fun updateArticle(article: Article)

    @Delete
    suspend fun deleteArticle(article: Article)
}