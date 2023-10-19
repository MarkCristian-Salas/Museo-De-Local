package com.example.museodelokal

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.museodelokal.uitel.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    private lateinit var in_Email : EditText
    private lateinit var in_Pass : EditText
    private lateinit var bt_register : Button
    private lateinit var bt_login : Button
    private lateinit var cb_remember : CheckBox
    private lateinit var tv_saved : TextView

    private lateinit var fname : String
    private lateinit var lname : String
    private lateinit var username : String

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var mAuth : FirebaseAuth



    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        in_Email = findViewById(R.id.et_email)
        in_Pass = findViewById(R.id.et_password)

        bt_register = findViewById(R.id.bt_createAcc)
        bt_login = findViewById(R.id.bt_procced)
        cb_remember = findViewById(R.id.cb_rememberme)
        tv_saved = findViewById(R.id.bt_AccSaved)

        mAuth = FirebaseAuth.getInstance()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("accounts")
        databaseHelper = DatabaseHelper(this)


        bt_login.setOnClickListener {
            val email = in_Email.text.toString()
            val password = in_Pass.text.toString()

            if(email.isNotEmpty() || password.isNotEmpty()){
                logIn(email, password)
            }
            else{
                Toast.makeText(this@LoginActivity, "Fill Out All Fields", Toast.LENGTH_SHORT).show()
            }
        }
        bt_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            finish()
        }

        tv_saved.setOnClickListener{
            val listItem=ArrayList<String>()
            val listItem2=ArrayList<String>()
            val artDialogBuilder = AlertDialog.Builder(this@LoginActivity)
            artDialogBuilder.setTitle("One tap Login")
            artDialogBuilder.setCancelable(true)
            val helper = DatabaseHelper(applicationContext)
            val db = helper.readableDatabase
            val rs = db.rawQuery("SELECT * FROM SavedAccounts",null)
            var index = 0
            while(rs.moveToNext()) {
                listItem.add(rs.getString(0).toString())
                listItem2.add(rs.getString(1).toString())
            }
            val list_array: Array<String> = listItem.toTypedArray()
            val list_array2: Array<String> = listItem2.toTypedArray()
            artDialogBuilder.setSingleChoiceItems(list_array, -1) { dialogInterface, i ->
                index = i
            }
            artDialogBuilder.setPositiveButton("Login"){_,_ ->
                logIn(list_array[index],list_array2[index])

            }
            artDialogBuilder.setNegativeButton("Remove"){_,_ ->
                databaseHelper.deleteData(list_array[index])
            }
            artDialogBuilder.setNeutralButton("Back"){_,_ ->


            }
            val alertDialogBox = artDialogBuilder.create()
            alertDialogBox.show()
        }


    }
    private fun logIn(email: String, password: String){
        val loading = LoadingDialog(this)
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    if(cb_remember.isChecked){
                        Toast.makeText(this@LoginActivity, "Log In Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                        val save = "true"
                        intent.putExtra("user",email)
                        intent.putExtra("pass",password)
                        intent.putExtra("save",save)

                        loading.startLoading()
                        val Handler = Handler()
                        Handler.postDelayed(object: Runnable{
                            override fun run() {

                                startActivity(intent)
                                finish()
                                loading.isDismiss()
                            }

                        },5000)

                    }

                    else{
                        Toast.makeText(this@LoginActivity, "Log In Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                        intent.putExtra("user",email)

                        loading.startLoading()
                        val Handler = Handler()
                        Handler.postDelayed(object: Runnable{
                            override fun run() {
                                startActivity(intent)
                                finish()
                                loading.isDismiss()
                            }

                        },3000)

                    }
                }
                else{
                    Toast.makeText(this@LoginActivity, "Log In Failed", Toast.LENGTH_SHORT).show()
                }

        }
    }
    private fun readData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts")
        databaseReference.child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            if (it.exists()) {
                val Fname = it.child("fname").value
                val Lname = it.child("lname").value
                val Username = it.child("username").value

                fname = Fname.toString().uppercase()
                lname = Lname.toString().uppercase()
                username = Username.toString()

                val Email = in_Email.text.toString()
                val Password = in_Pass.text.toString()
                val FNAME = fname
                val LNAME = lname
                val USERNAME = username
                val save = User(Email,Password,FNAME,LNAME,USERNAME)
                databaseHelper.insertUser(save)
            }
        }
    }
}