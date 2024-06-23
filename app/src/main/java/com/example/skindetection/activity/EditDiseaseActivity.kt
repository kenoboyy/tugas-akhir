package com.example.skindetection.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.roomdb.room.Constant
import com.example.skindetection.R
import com.example.skindetection.room.AppDB
import com.example.skindetection.room.Disease
import kotlinx.android.synthetic.main.activity_edit_disease.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditDiseaseActivity : AppCompatActivity() {

    lateinit var back : ImageView

    private val db by lazy { AppDB(this) }
    private var predictionResult: Int = 0 // Variabel untuk menyimpan hasil prediksi
    private var diseaseId = 0
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_disease)
        setupView()
        setupListener()

        predictionResult = intent.getIntExtra("prediction_result", 0)

        back = findViewById(R.id.back_btn)

        back.setOnClickListener {
            if (!isLoggedIn()) {
                // Jika tidak login, kembalikan ke DetectSkinActivity
                val backDestination = Intent(this, DetectSkinActivity::class.java)
                startActivity(backDestination)
            } else {
                // Jika login, kembalikan ke DiseaseActivity
                finish()
            }
        }

        if (intentType() == Constant.TYPE_READ) {
            getDiseaseByPrediction(predictionResult)
        }
    }

    private fun setupView() {
        when (intentType()) {
            Constant.TYPE_CREATE -> {
                save_button.visibility = View.VISIBLE
                update_button.visibility = View.GONE
                view_disease_name.visibility = View.GONE
                view_disease_about.visibility = View.GONE
                view_disease_prevent.visibility = View.GONE
                view_recommend_med.visibility = View.GONE
                view_disease_seedoctor.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                save_button.visibility = View.GONE
                update_button.visibility = View.GONE
                edit_disease_name.visibility = View.GONE
                edit_disease_about.visibility = View.GONE
                edit_disease_prevent.visibility = View.GONE
                edit_recommend_med.visibility = View.GONE
                edit_disease_see_doctor.visibility = View.GONE
                getDisease()
            }
            Constant.TYPE_UPDATE -> {
                save_button.visibility = View.GONE
                update_button.visibility = View.VISIBLE
                view_disease_name.visibility = View.GONE
                view_disease_about.visibility = View.GONE
                view_disease_prevent.visibility = View.GONE
                view_recommend_med.visibility = View.GONE
                view_disease_seedoctor.visibility = View.GONE
                getDisease()
            }
        }
    }

    private fun setupListener() {
        save_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.diseaseDao().addDisease(
                    Disease(
                        0,
                        edit_title.text.toString(),
                        edit_about.text.toString(),
                        edit_prevent.text.toString(),
                        imageUrl ?: "",
                        edit_med_txt.text.toString(),
                        edit_see_doctor.text.toString()
                    )
                )
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }
        update_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.diseaseDao().updateDisease(
                    Disease(
                        diseaseId,
                        edit_title.text.toString(),
                        edit_about.text.toString(),
                        edit_prevent.text.toString(),
                        imageUrl ?: "",
                        edit_med_txt.text.toString(),
                        edit_see_doctor.text.toString()
                    )
                )
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }
        edit_med_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            imageUrl = selectedImage.toString()
            Glide.with(this).load(imageUrl).into(edit_med_img)
        }
    }

    private fun getDisease() {
        diseaseId = intent.getIntExtra("disease_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val disease = db.diseaseDao().getDisease(diseaseId).first() // Menggunakan first() pada hasil query
                withContext(Dispatchers.Main) {
                    if (disease != null) {
                        edit_title.setText(disease.name)
                        edit_about.setText(disease.about)
                        edit_prevent.setText(disease.prevent)
                        edit_med_txt.setText(disease.med_txt)
                        edit_see_doctor.setText(disease.see_doctor)
                        view_title.text = disease.name
                        view_about.text = disease.about
                        view_prevent.text = disease.prevent
                        view_med_txt.text = disease.med_txt
                        view_see_doctor.text = disease.see_doctor
                        imageUrl = disease.med_img

                        Log.d("EditDiseaseActivity", "Image URL: $imageUrl")

                        Glide.with(this@EditDiseaseActivity)
                            .load(imageUrl)
                            .into(edit_med_img)

                        Glide.with(this@EditDiseaseActivity)
                            .load(imageUrl)
                            .into(view_med_img)
                    }
                }
            } catch (e: Exception) {
                Log.e("EditDiseaseActivity", "Error getting disease", e)
                withContext(Dispatchers.Main) {
                    // Handle error condition (optional)
                }
            }
        }
    }

    private fun getDiseaseByPrediction(predictionResult: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val disease = db.diseaseDao().getDiseaseByPrediction(predictionResult)
                withContext(Dispatchers.Main) {
                    if (disease != null) {
                        view_title.text = disease.name
                        view_about.text = disease.about
                        view_prevent.text = disease.prevent
                        view_med_txt.text = disease.med_txt
                        view_see_doctor.text = disease.see_doctor
                        imageUrl = disease.med_img
                        Glide.with(this@EditDiseaseActivity).load(imageUrl).into(view_med_img)
                    } else {
//                        Toast.makeText(this@EditDiseaseActivity, "Error retrieving disease data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@EditDiseaseActivity, "Error retrieving disease data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }

    private fun isLoggedIn(): Boolean {
        return getSharedPreferences("loginPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false)
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}
