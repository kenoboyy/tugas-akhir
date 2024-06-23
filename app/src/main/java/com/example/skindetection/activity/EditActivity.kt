package com.example.roomdb.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.roomdb.room.Constant
import com.example.skindetection.R
import com.example.skindetection.activity.ArticleActivity
import com.example.skindetection.room.Article
import com.example.skindetection.room.AppDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditActivity : AppCompatActivity() {

    lateinit var back : ImageView

    private val db by lazy { AppDB(this) }
    private var articleId = 0
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupView()
        setupListener()

        back = findViewById(R.id.back_btn)

        back.setOnClickListener {
            val backDestination = Intent(this, ArticleActivity::class.java)
            startActivity(backDestination)
        }
    }

    private fun setupView() {
        when (intentType()) {
            Constant.TYPE_CREATE -> {
                save_button.visibility = View.VISIBLE
                update_button.visibility = View.GONE
                view_title.visibility = View.GONE
                view_author.visibility = View.GONE
                view_content.visibility = View.GONE
                view_image.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                save_button.visibility = View.GONE
                update_button.visibility = View.GONE
                edit_article_title.visibility = View.GONE
                edit_article_author.visibility = View.GONE
                edit_article_content.visibility = View.GONE
                edit_image.visibility = View.GONE
                edit_image_info.visibility = View.GONE
                getArticle()
            }
            Constant.TYPE_UPDATE -> {
                save_button.visibility = View.GONE
                update_button.visibility = View.VISIBLE
                view_title.visibility = View.GONE
                view_author.visibility = View.GONE
                view_content.visibility = View.GONE
                view_image.visibility = View.GONE
                getArticle()
            }
        }
    }

    private fun setupListener() {
        save_button.setOnClickListener {
            if (isInputValid()) {
                CoroutineScope(Dispatchers.IO).launch {
                    db.articleDao().addArticle(
                        Article(
                            0,
                            edit_title.text.toString(),
                            edit_author.text.toString(),
                            edit_content.text.toString(),
                            imageUrl ?: ""
                        )
                    )
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            } else {
                showValidationError()
            }
        }

        update_button.setOnClickListener {
            if (isInputValid()) {
                CoroutineScope(Dispatchers.IO).launch {
                    db.articleDao().updateArticle(
                        Article(
                            articleId,
                            edit_title.text.toString(),
                            edit_author.text.toString(),
                            edit_content.text.toString(),
                            imageUrl ?: ""
                        )
                    )
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            } else {
                showValidationError()
            }
        }

        edit_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            imageUrl = selectedImage.toString()
            Glide.with(this).load(imageUrl).into(edit_image)
        }
    }

    private fun getArticle() {
        articleId = intent.getIntExtra("article_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val article = db.articleDao().getArticle(articleId).first()
            withContext(Dispatchers.Main) {
                edit_title.setText(article.title)
                edit_author.setText(article.author)
                edit_content.setText(article.content)
                view_title.text = article.title
                view_author.text = article.author
                view_content.text = article.content
                imageUrl = article.imageUrl
                Glide.with(this@EditActivity).load(imageUrl).into(edit_image)
                Glide.with(this@EditActivity).load(imageUrl).into(view_image)
            }
        }
    }

    private fun isInputValid(): Boolean {
        return edit_title.text.isNotEmpty() &&
                edit_author.text.isNotEmpty() &&
                edit_content.text.isNotEmpty() &&
                !imageUrl.isNullOrEmpty()
    }

    private fun showValidationError() {
        Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}