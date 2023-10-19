package com.example.museodelokal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.museodelokal.uitel.LoadingDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChangePassActivity : AppCompatActivity() {

    private lateinit var Currentpass : EditText
    private lateinit var Newpass : EditText
    private lateinit var ConNewpass : EditText
    private lateinit var Update : Button
    private lateinit var Cancel : Button

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var mAuth : FirebaseAuth

    private lateinit var userFinal : String
    private lateinit var passFinal : String

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        toolbar = findViewById(R.id.topnavi)
        drawerLayout = findViewById(R.id.drawer_layout)
        Currentpass = findViewById(R.id.et_recentPass)
        Newpass = findViewById(R.id.et_newpassword)
        ConNewpass = findViewById(R.id.et_connewpass)
        Update = findViewById(R.id.bt_Update)
        Cancel = findViewById(R.id.bt_cancel)

        databaseHelper = DatabaseHelper(this)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("accounts")
        mAuth = FirebaseAuth.getInstance()

        userFinal = intent.getStringExtra("user").toString()
        passFinal = intent.getStringExtra("pass").toString()

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("user",userFinal)
                    intent.putExtra("pass",passFinal)
                    startActivity(intent)
                    finish()

                }
                R.id.savedAcc -> {
                    startActivity(Intent(this,SavedAccountsActivity::class.java))
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
                    val intent = Intent(this, EditProfileActivity::class.java)
                    intent.putExtra("user",userFinal)
                    intent.putExtra("pass",passFinal)
                    startActivity(intent)
                    finish()
                }
                R.id.Change_password -> {
                    drawerLayout.close()
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
                                    startActivity(Intent(this@ChangePassActivity, LoginActivity::class.java).putExtra("user", userFinal))
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

        Update.setOnClickListener {
            val loading = LoadingDialog(this)
            if(Currentpass.text.toString() == passFinal){
                if(Newpass.text.toString().length >= 8) {
                    if (Newpass.text.toString() == ConNewpass.text.toString()) {
                        databaseHelper.updateData(Newpass.text.toString(),userFinal)
                        updateData()
                        mAuth.currentUser?.updatePassword(Newpass.text.toString())
                        Toast.makeText(this, "Password Changed", Toast.LENGTH_SHORT).show()
                        loading.startLoading()
                        val Handler = Handler()
                        Handler.postDelayed(object : Runnable {
                            override fun run() {
                                startActivity(Intent(this@ChangePassActivity, ProfileActivity::class.java).putExtra("user", userFinal))
                                finish()
                                loading.isDismiss()
                            }
                        }, 3000)
                    } 
                    else {
                        Toast.makeText(this, "Password Doesn't Matched", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this, "Enter Password More than 8 Characters", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this, "Please enter Current Password", Toast.LENGTH_SHORT).show()
            }

        }
        Cancel.setOnClickListener {
            startActivity(Intent(this@ChangePassActivity, ProfileActivity::class.java).putExtra("user", userFinal))
            finish()
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.topnavi, menu)
        return true
    }

    private fun updateData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        val user = mapOf<String,String>(
            "password" to Newpass.text.toString()
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
}