package com.example.museodelokal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DetailActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var detailDesc : TextView
    private lateinit var detailTitle : TextView
    private lateinit var detailImage: ImageView
    private lateinit var detailPrice : TextView
    private lateinit var detailUploader: TextView
    //step 5 delete button
    private var key = ""
    private var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        back = findViewById(R.id.back_button)
        detailPrice = findViewById(R.id.detailPrice)
        detailDesc = findViewById(R.id.detailDesc)
        detailTitle = findViewById(R.id.detailTitle)
        detailImage = findViewById(R.id.detailImage)
        detailUploader = findViewById(R.id.detailUploader)

        back.setOnClickListener {
            back.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        val bundle = intent.extras
        if (bundle != null) {
            detailDesc.text = bundle.getString("Description")
            detailTitle.text = bundle.getString("Title")
            detailPrice.text = bundle.getString("Price")
            detailUploader.text = bundle.getString("Uploader")
            //step 6 delete button
            key = bundle.getString("Key") ?: ""
            imageUrl = bundle.getString("Image") ?: ""
            Glide.with(this).load(bundle.getString("Image")).into(detailImage)
        }

    }
}