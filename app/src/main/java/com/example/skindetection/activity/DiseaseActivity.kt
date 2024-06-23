package com.example.skindetection.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdb.room.Constant
import com.example.skindetection.R
import com.example.skindetection.room.AppDB
import com.example.skindetection.room.Disease
import kotlinx.android.synthetic.main.activity_disease.*
import kotlinx.coroutines.*

class DiseaseActivity : AppCompatActivity() {
    lateinit var back: ImageView

    private val db by lazy { AppDB(this) }
    lateinit var diseaseAdapter: DiseaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease)
//        setupListener()
        setupRecyclerView()

        back = findViewById(R.id.back_btn)

        back.setOnClickListener {
            val backDestination = Intent(this, DetectSkinActivity::class.java)
            startActivity(backDestination)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            val diseases = db.diseaseDao().getDiseases()
            withContext(Dispatchers.Main) {
                diseaseAdapter.setData(diseases)
                diseaseAdapter.notifyDataSetChanged()
            }
        }
    }

//    private fun setupListener(){
//        add_disease.setOnClickListener {
//            intentEdit(Constant.TYPE_CREATE, 0)
//        }
//    }

    private fun setupRecyclerView () {
        diseaseAdapter = DiseaseAdapter(
            arrayListOf(),
            object : DiseaseAdapter.OnAdapterListener {
                override fun onClick(disease: Disease) {
                    intentView(Constant.TYPE_READ, disease.id)
                }

                override fun onUpdate(disease: Disease) {
                    intentEdit(Constant.TYPE_UPDATE, disease.id)
                }

                override fun onDelete(disease: Disease) {
                    deleteAlert(disease)
                }
            })

        list_disease.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = diseaseAdapter
        }
    }

    private fun intentEdit(intent_type: Int, disease_id: Int) {
        startActivity(
            Intent(this, EditDiseaseActivity::class.java)
                .putExtra("intent_type", intent_type)
                .putExtra("disease_id", disease_id)
        )
    }

    private fun intentView(intent_type: Int, disease_id: Int) {
        startActivity(
            Intent(this, EditDiseaseActivity::class.java)
                .putExtra("intent_type", intent_type)
                .putExtra("disease_id", disease_id)
        )
    }

    private fun deleteAlert(disease: Disease){
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Delete Confirmation")
            setMessage("Are you sure want to delete ${disease.name}?")
            setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete") { dialogInterface, i ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.diseaseDao().deleteDisease(disease)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }
        dialog.show()
    }
}
