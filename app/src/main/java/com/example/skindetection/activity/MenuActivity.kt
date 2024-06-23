package com.example.roomdb.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.skindetection.R
import com.example.skindetection.activity.ArticleActivity
import com.example.skindetection.activity.LoginActivity
import com.example.skindetection.activity.DetectSkinActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var articleBtn: ImageButton
    private lateinit var detectSkinBtn: ImageButton
    private lateinit var loginLogoutButton: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        articleBtn = findViewById(R.id.articleBtn)
        detectSkinBtn = findViewById(R.id.detectSkinBtn)
        loginLogoutButton = findViewById(R.id.loginadmin)

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        articleBtn.setOnClickListener {
            val articleDestination = Intent(this, ArticleActivity::class.java)
            startActivity(articleDestination)
        }

        detectSkinBtn.setOnClickListener {
            val detectSkinDestination = Intent(this, DetectSkinActivity::class.java)
            startActivity(detectSkinDestination)
        }

        loginLogoutButton.setOnClickListener {
            // Jika pengguna sudah login, jalankan fungsi logout
            if (isLoggedIn()) {
                logout()
            } else {
                // Jika pengguna belum login, buka halaman login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // Periksa status login saat aplikasi dimulai
        checkLoginStatus()
    }

    override fun onResume() {
        super.onResume()
        // Periksa dan atur kembali status login setiap kali aktivitas kembali ke foreground
        checkLoginStatus()
    }

    private fun isLoggedIn(): Boolean {
        // Cek status login dari SharedPreferences
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun logout() {
        // Hapus status login dari SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Kembali ke halaman login setelah logout
        val intent = Intent(this@MenuActivity, MenuActivity::class.java)
        startActivity(intent)
        finish() // Tutup activity menu setelah logout
    }

    private fun checkLoginStatus() {
        // Cek status login dari SharedPreferences
        val isLoggedIn = isLoggedIn()

        if (isLoggedIn) {
            // Jika pengguna sudah login, sembunyikan TextView "Sign in as Admin"
            loginLogoutButton.text = "Logout"
        } else {
            // Jika pengguna belum login, tampilkan TextView "Sign in as Admin"
            loginLogoutButton.text = "Sign in as Admin"
        }
    }
}
