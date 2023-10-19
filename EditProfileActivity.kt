package com.example.museodelokal

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.museodelokal.uitel.LoadingDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var ep_username : EditText
    private lateinit var ep_bio : EditText
    private lateinit var update : Button
    private lateinit var cancel : Button
    private lateinit var imgprofile : ImageView

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var firebasbeStorage: FirebaseStorage
    private lateinit var mAuth : FirebaseAuth

    private lateinit var userFinal : String
    private lateinit var passFinal : String

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var finalPass : String
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ep_username = findViewById(R.id.et_username)
        ep_bio = findViewById(R.id.et_bio)
        update = findViewById(R.id.bt_Update)
        cancel = findViewById(R.id.bt_cancel)
        imgprofile = findViewById(R.id.imageView)

        databaseHelper = DatabaseHelper(this)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("accounts")
        firebasbeStorage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        userFinal = intent.getStringExtra("user").toString()
        passFinal = intent.getStringExtra("pass").toString()

        /* Drawer */
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.topnavi)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        var checkDP = false
        /* EDIT PROFILE */
        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {it ->
                imgprofile.setImageURI(it)
                if (it != null) {
                    uri = it

                }
            })

        imgprofile.setOnClickListener {
            galleryImage.launch("image/*")
            checkDP = true
        }
        update.setOnClickListener {
            if(checkDP == true) {
                firebasbeStorage.getReference("images").child(System.currentTimeMillis().toString())
                    .putFile(uri)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {
                                val mapImage = mapOf(
                                    "url" to it.toString()
                                )
                                databaseReference =
                                    FirebaseDatabase.getInstance().getReference("userImage")
                                databaseReference.child(mAuth.currentUser?.uid!!).setValue(mapImage)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Profile Upload Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { error ->
                                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                    }

                if (ep_username.text.toString().isNotEmpty()) {
                    val loading = LoadingDialog(this)
                    updateData()
                    databaseHelper.updateUsername(ep_username.text.toString(),userFinal)
                    Toast.makeText(this, "Profile Successfully Edited", Toast.LENGTH_SHORT).show()
                    loading.startLoading()
                    val Handler = Handler()
                    Handler.postDelayed(object : Runnable {
                        override fun run() {
                            startActivity(
                                Intent(
                                    this@EditProfileActivity,
                                    ProfileActivity::class.java
                                ).putExtra("user", userFinal)
                            )
                            finish()
                            loading.isDismiss()
                        }
                    }, 3000)
                }
                else {
                    val loading = LoadingDialog(this)
                    Toast.makeText(this, "Profile Successfully Edited", Toast.LENGTH_SHORT).show()
                    loading.startLoading()
                    val Handler = Handler()
                    Handler.postDelayed(object : Runnable {
                        override fun run() {
                            startActivity(
                                Intent(
                                    this@EditProfileActivity,
                                    ProfileActivity::class.java
                                ).putExtra("user", userFinal)
                            )
                            finish()
                            loading.isDismiss()
                        }
                    }, 3000)
                }
                checkDP = false
            }
            else {
                if (ep_username.text.toString().isNotEmpty()) {
                    val loading = LoadingDialog(this)
                    updateData()
                    databaseHelper.updateUsername(ep_username.text.toString(),userFinal)
                    Toast.makeText(this, "Profile Successfully Edited", Toast.LENGTH_SHORT).show()
                    loading.startLoading()
                    val Handler = Handler()
                    Handler.postDelayed(object : Runnable {
                        override fun run() {
                            startActivity(
                                Intent(
                                    this@EditProfileActivity,
                                    ProfileActivity::class.java
                                ).putExtra("user", userFinal)
                            )
                            finish()
                            loading.isDismiss()
                        }
                    }, 3000)
                } else {
                    val loading = LoadingDialog(this)
                    Toast.makeText(this, "Profile Successfully Edited", Toast.LENGTH_SHORT).show()
                    loading.startLoading()
                    val Handler = Handler()
                    Handler.postDelayed(object : Runnable {
                        override fun run() {
                            startActivity(
                                Intent(
                                    this@EditProfileActivity,
                                    ProfileActivity::class.java
                                ).putExtra("user", userFinal)
                            )
                            finish()
                            loading.isDismiss()
                        }
                    }, 3000)
                }
            }
        }
        cancel.setOnClickListener {
            startActivity(Intent(this@EditProfileActivity, ProfileActivity::class.java).putExtra("user", userFinal))
            finish()
        }
        /* Navigation */
        navigationView = findViewById(R.id.nav_view)
        val artDialogBuilder = AlertDialog.Builder(this)
        val loading = LoadingDialog(this)
        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("user",userFinal)
                    startActivity(intent)
                    finish()
                }
                R.id.message -> {

                }
                R.id.gallery -> {
                    val intent = Intent(this, GalleryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.create_post -> {
                    val intent = Intent(this, CreateActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.edit_profile -> {
                    drawerLayout.close()
                }
                R.id.Change_password -> {
                    readData()
                    val intent = Intent(this, ChangePassActivity::class.java)
                    intent.putExtra("user",userFinal.toString())
                    intent.putExtra("pass",passFinal.toString())
                    startActivity(intent)
                    finish()
                }
                R.id.deleteAcc -> {

                    artDialogBuilder.setTitle("Delete Account")
                    artDialogBuilder.setMessage("Are you sure you want to Delete you account?")
                    artDialogBuilder.setCancelable(false)
                    artDialogBuilder.setPositiveButton("Yes"){_,_ ->
                        artDialogBuilder.setTitle("Delete Account")
                        artDialogBuilder.setMessage("Are you sure you really sure?")
                        artDialogBuilder.setCancelable(false)
                        artDialogBuilder.setPositiveButton("Yes"){_,_ ->
                            databaseHelper.deleteData(userFinal.toString())
                            deleteData()
                            mAuth.currentUser?.delete()
                            loading.startLoading()
                            val Handler = Handler()
                            Handler.postDelayed(object : Runnable {
                                override fun run() {
                                    startActivity(Intent(this@EditProfileActivity, LoginActivity::class.java).putExtra("user", userFinal))
                                    finish()
                                    loading.isDismiss()
                                }
                            }, 3000)
                        }
                        artDialogBuilder.setNegativeButton("No"){_,_ ->

                        }
                        val alertDialogBox = artDialogBuilder.create()
                        alertDialogBox.show()
                    }
                    artDialogBuilder.setNegativeButton("No"){_,_ ->

                    }
                    val alertDialogBox = artDialogBuilder.create()
                    alertDialogBox.show()
                }
                R.id.savedAcc -> {
                    startActivity(Intent(this,SavedAccountsActivity::class.java))
                }
                R.id.about_us -> {

                }
                R.id.logout -> {
                    artDialogBuilder.setTitle("Log Out")
                    artDialogBuilder.setMessage("Are you sure you want to sign out?")
                    artDialogBuilder.setCancelable(false)
                    artDialogBuilder.setPositiveButton("Yes"){_,_ ->
                        startActivity(Intent(this,LoginActivity::class.java))
                        finish()
                    }
                    artDialogBuilder.setNegativeButton("No"){_,_ ->

                    }
                    val alertDialogBox = artDialogBuilder.create()
                    alertDialogBox.show()
                }
            }
            true
        }
    }
    private fun readData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            if(it.exists()){
                val pass = it.child("password").value
                val Username = it.child("username").value
                val Bio = it.child("bio").value
                finalPass = pass.toString()
                ep_username.setText(Username.toString())
                ep_bio.setText(Bio.toString())
            }
        }
    }

    private fun updateData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        val user = mapOf<String,String>(
            "username" to ep_username.text.toString(),
            "bio" to ep_bio.text.toString()
        )

        databaseReference.child(mAuth.currentUser?.uid!!).updateChildren(user).addOnSuccessListener {
            Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Failed To Update", Toast.LENGTH_SHORT).show()
        }
    }
    private fun deleteData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.child(mAuth.currentUser?.uid!!).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener{
                Toast.makeText(this, "Failed To Delete", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.topnavi, menu)
        return true
    }
}