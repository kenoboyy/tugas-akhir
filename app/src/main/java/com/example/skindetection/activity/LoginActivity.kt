package com.example.skindetection.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.roomdb.activity.MenuActivity
import com.example.skindetection.R
import com.example.skindetection.room.Admin
import com.example.skindetection.room.AppDB
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    lateinit var back: ImageView

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var db: AppDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = AppDB.invoke(this)

        loginUsername = findViewById(R.id.loginUsername)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)

        back = findViewById(R.id.back_btn)

        back.setOnClickListener {
            val backDestination = Intent(this, MenuActivity::class.java)
            startActivity(backDestination)
        }

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            val username = loginUsername.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val admin = db.adminDao().getUser(username, password)
                if (admin != null) {
                    // Login berhasil, simpan status login ke SharedPreferences
                    saveLoginStatus()

                    // Lanjut ke halaman berikutnya (contoh: MenuActivity)
                    val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

//        val toRegisterTextView = findViewById<TextView>(R.id.toregister)
//        toRegisterTextView.setOnClickListener {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun saveLoginStatus() {
        // Menggunakan editor untuk menyimpan status login ke SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }
}
