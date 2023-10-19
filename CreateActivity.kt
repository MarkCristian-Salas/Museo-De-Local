package com.example.museodelokal

import android.content.Intent
import android.icu.text.DateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class CreateActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var imageUri : Uri
    private lateinit var upload_post: ImageView
    private lateinit var create : Button
    private lateinit var title : EditText
    private lateinit var description : EditText
    private lateinit var price : EditText
    private lateinit var imageUrl: String
    private lateinit var mAuth : FirebaseAuth
    private var imageSelected = false
    private lateinit var databaseReference : DatabaseReference
    private lateinit var fname: String
    private lateinit var lname : String
    private lateinit var fullname : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        mAuth = FirebaseAuth.getInstance()

        title = findViewById(R.id.cp_title)
        description = findViewById(R.id.cp_description)
        price = findViewById(R.id.cp_price)
        create = findViewById(R.id.create_post_button)
        upload_post = findViewById(R.id.upload_photo)
        back = findViewById(R.id.back_button)
        databaseReference = FirebaseDatabase.getInstance().reference.child("accounts")

        back.setOnClickListener {
            back.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                imageUri = data!!.data!!
                upload_post.setImageURI(imageUri)
                imageSelected = true
            } else {
                Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show()
            }
        }

        upload_post.setOnClickListener { view: View? ->
            val photoPicker = Intent()
            photoPicker.action = Intent.ACTION_GET_CONTENT
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        create.setOnClickListener {
            if(validateInputs() && imageSelected){
                readData()
                saveData()
                Toast.makeText(this, "Done Posting", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, if (!imageSelected) "Please select and image " else "Please fill out all fields",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateInputs(): Boolean {
        val titleText = title.text.toString()
        val descriptionText = description.text.toString()
        val priceText = price.text.toString()

        return titleText.isNotEmpty() && descriptionText.isNotEmpty() && priceText.isNotEmpty()
    }

    private fun saveData() {
        val storageReference = FirebaseStorage.getInstance().getReference("uploadImages")
            .child(imageUri.lastPathSegment.toString());

        val builder = AlertDialog.Builder(this@CreateActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.loading)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                uriTask.addOnCompleteListener { downloadTask ->
                    if (downloadTask.isSuccessful) {
                        val urlImage = downloadTask.result.toString()
                        imageUrl = urlImage
                        dialog.dismiss()
                        uploadData()


                    } else {
                        // Handle the case when getting the download URL fails
                        dialog.dismiss()
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle the case when uploading the image fails
                dialog.dismiss()
            }
    }

    private fun uploadData() {
        val title = title.text.toString()
        val priceStr = price.text.toString()
        val desc = description.text.toString()
        val fullname = fullname
        priceStr.toIntOrNull() ?: 0

        val dataUpload = Data_Upload(title, priceStr.toLong(), desc, imageUrl,fullname)
        val databaseReference = FirebaseDatabase.getInstance().getReference("Uploads").child("${mAuth.currentUser?.uid!!} $title")

        databaseReference.setValue(dataUpload)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                    finish()
                    val intent = Intent(this, GalleryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
    private fun readData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            if(it.exists()){
                val Fname = it.child("fname").value
                val Lname = it.child("lname").value
                fname = Fname.toString().uppercase()
                lname = Lname.toString().uppercase()
                val FullName = "$lname, $fname"
                fullname = FullName
                
            }
        }
    }

}