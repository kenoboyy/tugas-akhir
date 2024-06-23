package com.example.skindetection.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdb.activity.EditActivity
import com.example.roomdb.activity.MenuActivity
import com.example.roomdb.room.Constant
import com.example.skindetection.R
import com.example.skindetection.room.AppDB
import com.example.skindetection.room.Article
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleActivity : AppCompatActivity() {

    private lateinit var back: ImageView
    private lateinit var buttonCreate: Button
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences

    private val db by lazy { AppDB(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        // Deklarasi variabel dengan id dari View
        back = findViewById(R.id.back_btn)
        buttonCreate = findViewById(R.id.button_create)
        recyclerView = findViewById(R.id.list_article)

        // Variabel Login
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        back.setOnClickListener {
            val backDestination = Intent(this, MenuActivity::class.java)
            startActivity(backDestination)
        }

        buttonCreate.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
        }

        setupRecyclerView()

        // Load data saat Activity dibuka
        loadData()
    }

    override fun onResume() {
        super.onResume()

        // Periksa dan atur kembali status login setiap kali aktivitas kembali ke foreground
        checkLoginStatus()

        // Load data jika status login valid
        if (isLoggedIn()) {
            loadData()
        }
    }

    private fun checkLoginStatus() {
        // Cek status login dari SharedPreferences
        val isLoggedIn = isLoggedIn()

        if (isLoggedIn) {
            // Jika pengguna sudah login, tampilkan tombol create
            buttonCreate.visibility = View.VISIBLE
        } else {
            // Jika pengguna belum login, sembunyikan tombol create
            buttonCreate.visibility = View.GONE
        }
    }

    private fun isLoggedIn(): Boolean {
        // Cek status login dari SharedPreferences
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val articles = db.articleDao().getArticles()
            withContext(Dispatchers.Main) {
                articleAdapter.setData(articles)
                articleAdapter.notifyDataSetChanged()
            }
        }
    }

    //Fungsi memanggil RecyclerView
    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(
            arrayListOf(),
            object : ArticleAdapter.OnAdapterListener {
                override fun onClick(article: Article) {
                    intentView(Constant.TYPE_READ, article.id)
                }

                override fun onUpdate(article: Article) {
                    intentEdit(Constant.TYPE_UPDATE, article.id)
                }

                override fun onDelete(article: Article) {
                    deleteAlert(article)
                }
            })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = articleAdapter
        }
    }

    private fun intentEdit(intentType: Int, articleId: Int) {
        startActivity(
            Intent(this, EditActivity::class.java)
                .putExtra("intent_type", intentType)
                .putExtra("article_id", articleId)
        )
    }

    private fun intentView(intentType: Int, articleId: Int) {
        startActivity(
            Intent(this, EditActivity::class.java)
                .putExtra("intent_type", intentType)
                .putExtra("article_id", articleId)
        )
    }

    //konfirmasi untuk menghapus artikel
    private fun deleteAlert(article: Article) {
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Delete Confirmation")
            setMessage("Are you sure want to delete ${article.title}?")
            setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete") { dialogInterface, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.articleDao().deleteArticle(article)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }

        dialog.show()
    }
}
