package com.example.skindetection.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.roomdb.activity.MenuActivity
import com.example.roomdb.room.Constant
import com.example.skindetection.R
import com.example.skindetection.ml.Skindetection
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class DetectSkinActivity : AppCompatActivity() {

    lateinit var back: ImageView
    lateinit var add_disease : TextView
    lateinit var select_image_button : FloatingActionButton
    lateinit var make_prediction : Button
    lateinit var img_view : ImageView
    lateinit var camerabtn : FloatingActionButton
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_skin)

        back = findViewById(R.id.back_btn)
        back.setOnClickListener {
            val backDestination = Intent(this, MenuActivity::class.java)
            startActivity(backDestination)
        }

        add_disease = findViewById(R.id.add_disease)
        add_disease.setOnClickListener{
            startActivity(Intent(this,DiseaseActivity::class.java))
        }

        select_image_button = findViewById(R.id.gallery)
        make_prediction = findViewById(R.id.predict)
        img_view = findViewById(R.id.img_placeholder)
        camerabtn = findViewById(R.id.camera)

        // Handling permissions
        checkAndRequestPermissions()

        // Unggah gambar dari galeri
        select_image_button.setOnClickListener {
            Log.d("mssg", "button pressed")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 250)
        }
        // Ambil gambar dari kamera
        camerabtn.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 200)
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        make_prediction.setOnClickListener {
            if (bitmap == null) {
                // Tampilkan toast jika bitmap null
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
            else {
                var tensorImage = TensorImage(DataType.FLOAT32)

                tensorImage.load(bitmap)
                tensorImage = imageProcessor.process(tensorImage)

                val model = Skindetection.newInstance(this)

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(tensorImage.buffer)

                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                var output = 0
                outputFeature0.forEachIndexed { index, fl ->
                    if (outputFeature0[output] < fl) {
                        output = index + 1
                    }
                }
                model.close()

                val intent = Intent(this, EditDiseaseActivity::class.java)
                intent.putExtra("prediction_result", output)
                intent.putExtra("intent_type", Constant.TYPE_READ)
                startActivity(intent)
            }
        }

        // Check login status to hide/show add_disease button
        checkLoginStatus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 250 && resultCode == Activity.RESULT_OK){
            img_view.setImageURI(data?.data)

            val uri : Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }
        else if(requestCode == 200 && resultCode == Activity.RESULT_OK){
            bitmap = data?.extras?.get("data") as Bitmap
            img_view.setImageBitmap(bitmap)
        }
    }

    private fun checkAndRequestPermissions() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }
    }

    private fun checkLoginStatus() {
        val isLoggedIn = getSharedPreferences("loginPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            add_disease.visibility = View.GONE
        }
    }
}
