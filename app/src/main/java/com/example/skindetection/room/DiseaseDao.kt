package com.example.roomdb.room

import androidx.room.*import com.example.skindetection.room.Disease

@Dao
interface DiseaseDao {
    @Insert
    suspend fun addDisease(disease: Disease)

    @Query("SELECT * FROM disease ORDER BY id DESC")
    suspend fun getDiseases(): List<Disease>

    @Query("SELECT * FROM disease WHERE id=:disease_id")
    suspend fun getDisease(disease_id: Int): Disease

    @Update
    suspend fun updateDisease(disease: Disease)

    @Delete
    suspend fun deleteDisease(disease: Disease)

    @Query("SELECT * FROM Disease WHERE id = :predictionResult LIMIT 1")
    suspend fun getDiseaseByPrediction(predictionResult: Int): Disease?
}