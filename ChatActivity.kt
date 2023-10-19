package com.example.museodelokal

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.museodelokal.uitel.LoadingDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<UserChat>
    private lateinit var adapter: UserAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var finalPass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userFinal = intent.getStringExtra("user")
        toolbar = findViewById(R.id.topnavi)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        setSupportActionBar(toolbar)
        val toggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter


        databaseReference = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()

        val artDialogBuilder = AlertDialog.Builder(this)
        val loading = LoadingDialog(this)

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("user", userFinal)
                    startActivity(intent)
                    finish()

                }

                R.id.message -> {
                    drawerLayout.close()
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
                    intent.putExtra("user", userFinal)
                    intent.putExtra("pass", finalPass)
                    startActivity(intent)
                    finish()
                }

                R.id.Change_password -> {
                    val intent = Intent(this, ChangePassActivity::class.java)
                    intent.putExtra("user", userFinal.toString())
                    intent.putExtra("pass", finalPass)
                    startActivity(intent)
                    finish()
                }

                R.id.deleteAcc -> {

                    artDialogBuilder.setTitle("Delete Account")
                    artDialogBuilder.setMessage("Are you sure you want to Delete you account?")
                    artDialogBuilder.setCancelable(false)
                    artDialogBuilder.setPositiveButton("Yes") { _, _ ->
                        artDialogBuilder.setTitle("Delete Account")
                        artDialogBuilder.setMessage("Are you sure you really sure?")
                        artDialogBuilder.setCancelable(false)
                        artDialogBuilder.setPositiveButton("Yes") { _, _ ->
                            databaseHelper.deleteData(userFinal.toString())
                            mAuth.currentUser?.delete()
                            loading.startLoading()
                            val Handler = Handler()
                            Handler.postDelayed(object : Runnable {
                                override fun run() {
                                    startActivity(
                                        Intent(
                                            this@ChatActivity,
                                            LoginActivity::class.java
                                        ).putExtra("user", userFinal)
                                    )
                                    finish()
                                    loading.isDismiss()
                                }
                            }, 3000)
                        }
                        artDialogBuilder.setNegativeButton("No") { _, _ ->

                        }
                        val alertDialogBox = artDialogBuilder.create()
                        alertDialogBox.show()
                    }
                    artDialogBuilder.setNegativeButton("No") { _, _ ->

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
                    artDialogBuilder.setPositiveButton("Yes") { _, _ ->
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    artDialogBuilder.setNegativeButton("No") { _, _ ->

                    }
                    val alertDialogBox = artDialogBuilder.create()
                    alertDialogBox.show()
                }
            }
            true
        }

        databaseReference.child("accounts").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(UserChat::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)

                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}