package com.example.skindetection.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Disease(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val about: String,
    val prevent: String,
    val med_img: String,
    val med_txt: String,
    val see_doctor: String
){
    // Contoh implementasi fungsi first() sebagai member function
    suspend fun first(): Disease {
        // Implementasi sesuai dengan logika yang diinginkan
        return this // Misalnya, kembalikan instance diri sendiri
    }
}