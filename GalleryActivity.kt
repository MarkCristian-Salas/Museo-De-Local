package com.example.museodelokal


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.museodelokal.uitel.LoadingDialog
import com.example.museodelokal.uitel.PostAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GalleryActivity : AppCompatActivity() {

    lateinit var gridView: GridView
    lateinit var dataList: ArrayList<Data_Upload>
    lateinit var adapter: PostAdapter
    private var DatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
    private lateinit var searchView: SearchView

    private lateinit var databaseHelper : DatabaseHelper
    private lateinit var databaseReference: DatabaseReference

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var userFinal : String
    private lateinit var passFinal : String
    private lateinit var finalPass : String

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        databaseHelper = DatabaseHelper(this)
        toolbar = findViewById(R.id.topnavi)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view1)
        gridView = findViewById(R.id.gridView)
        dataList = ArrayList<Data_Upload>()
        adapter = PostAdapter(this, dataList)
        gridView.adapter = (adapter)
        searchView = findViewById(R.id.searchView)
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        mAuth = FirebaseAuth.getInstance()

        userFinal = intent.getStringExtra("user").toString()
        passFinal = intent.getStringExtra("pass").toString()


        readData()
        DatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (dataSnapshot in snapshot.children) {
                    val dataUpload = dataSnapshot.getValue(Data_Upload::class.java)
                    dataUpload?.key = dataSnapshot.key;
                    if (dataUpload != null) {
                        dataList.add(dataUpload)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                searchList(query)
                return true
            }
        })

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
                    intent.putExtra("pass",finalPass)
                    startActivity(intent)
                    finish()
                }
                R.id.gallery -> {
                    drawerLayout.close()
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
                                    startActivity(Intent(this@GalleryActivity, LoginActivity::class.java).putExtra("user", userFinal))
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

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }




    fun searchList(text: String) {
        val searchList = ArrayList<Data_Upload>()
        for (dataUpload in dataList) {
            if (dataUpload.getuTitle().lowercase().contains(text.lowercase())) {
                searchList.add(dataUpload)
            }
        }
        adapter.searchDataList(searchList)
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun readData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            if (it.exists()) {
                val pass = it.child("password").value
                finalPass = pass.toString()
            }
        }
    }
}
