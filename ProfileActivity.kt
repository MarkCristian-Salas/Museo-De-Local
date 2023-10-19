package com.example.museodelokal

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.museodelokal.uitel.LoadingDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var fullname : TextView
    private lateinit var username : TextView
    private lateinit var bio : TextView
    private lateinit var fname : String
    private lateinit var lname : String
    private lateinit var Profile : ImageView
    private lateinit var finalPass : String

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebasbeStorage: FirebaseStorage
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var mAuth: FirebaseAuth

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        fullname = findViewById(R.id.tv_fullname)
        Profile = findViewById(R.id.img_profile)
        username = findViewById(R.id.tv_username)
        bio = findViewById(R.id.tv_bio)


        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("accounts")
        databaseHelper = DatabaseHelper(this)
        firebasbeStorage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val userFinal = intent.getStringExtra("user")
        val passFinal = intent.getStringExtra("pass")
        val save = intent.getStringExtra("save")
        readData()
        toolbar = findViewById(R.id.topnavi)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(save == "true"){
            saveData()
        }
        else{
            null
        }

        val artDialogBuilder = AlertDialog.Builder(this)
        val loading = LoadingDialog(this)

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    drawerLayout.close()
                }
                R.id.message -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("user",userFinal)
                    intent.putExtra("pass",finalPass)
                    startActivity(intent)
                    finish()
                }
                R.id.gallery -> {
                    val intent = Intent(this, GalleryActivity::class.java)
                    intent.putExtra("user",userFinal)
                    intent.putExtra("pass",finalPass)
                    startActivity(intent)
                    finish()
                }
                R.id.create_post -> {
                    val intent = Intent(this, CreateActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.edit_profile -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    intent.putExtra("user",userFinal)
                    intent.putExtra("pass",finalPass)
                    startActivity(intent)
                    finish()
                }
                R.id.Change_password -> {
                    val intent = Intent(this, ChangePassActivity::class.java)
                    intent.putExtra("user",userFinal.toString())
                    intent.putExtra("pass",finalPass)
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
                                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java).putExtra("user", userFinal))
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun readData(){

        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            if(it.exists()){
                val Fname = it.child("fname").value
                val Lname = it.child("lname").value
                val Username = it.child("username").value
                val Bio = it.child("bio").value
                val pass = it.child("password").value
                fname = Fname.toString().uppercase()
                lname = Lname.toString().uppercase()
                val FullName = "$lname, $fname"
                fullname.text = FullName
                username.text = Username.toString()
                bio.text = Bio.toString()
                finalPass = pass.toString()
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("userImage")
        databaseReference.child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            val url = it.child("url").value.toString()
            Glide.with(this).load(url).into(Profile)

        }.addOnFailureListener{error ->
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
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

    private fun saveData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            if (it.exists()) {
                val email = it.child("email").value
                val password = it.child("password").value
                val FNAME = it.child("fname").value
                val LNAME = it.child("lname").value
                val USERNAME = it.child("username").value

                val save = User(email.toString(),password.toString(),FNAME.toString(),LNAME.toString(),USERNAME.toString())
                databaseHelper.insertUser(save)
            }
        }
    }


}