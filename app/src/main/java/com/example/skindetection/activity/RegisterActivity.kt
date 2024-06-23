package com.example.skindetection.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roomdb.activity.MenuActivity
import com.example.skindetection.R
import com.example.skindetection.room.Admin
import com.example.skindetection.room.AppDB
import com.example.roomdb.room.AdminDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    lateinit var back: ImageView

    private lateinit var registerUsername: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerButton: Button

    private val db by lazy { AppDB.invoke(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerUsername = findViewById(R.id.registerUsername)
        registerPassword = findViewById(R.id.registerPassword)
        registerButton = findViewById(R.id.registerButton)

        back = findViewById(R.id.back_btn)

        back.setOnClickListener {
            val backDestination = Intent(this, MenuActivity::class.java)
            startActivity(backDestination)
        }

        registerButton.setOnClickListener {
            val username = registerUsername.text.toString().trim()
            val password = registerPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val existingAdmin = db.adminDao().getAdminByUsername(username)
                if (existingAdmin != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Username sudah ada", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Insert new admin into database
                    val newAdmin = Admin(username = username, password = password)
                    db.adminDao().insertAdmin(newAdmin)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        finish() // Finish activity after successful registration
                    }
                }
            }
        }

        val tologin = findViewById<TextView>(R.id.tologin)
        tologin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
