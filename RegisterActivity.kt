package com.example.museodelokal

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.museodelokal.uitel.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {

    private lateinit var in_Fname : EditText
    private lateinit var in_Lname : EditText
    private lateinit var in_username : EditText
    private lateinit var in_Email : EditText
    private lateinit var in_Pass : EditText
    private lateinit var in_ConPass : EditText
    private lateinit var bt_register : Button
    private lateinit var bt_login : Button

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        in_Fname = findViewById(R.id.et_fname)
        in_Lname = findViewById(R.id.et_lname)
        in_Email = findViewById(R.id.et_email)
        in_username = findViewById(R.id.et_username)
        in_Pass = findViewById(R.id.et_password)
        in_ConPass = findViewById(R.id.et_confirm_password)

        bt_register = findViewById(R.id.bt_register)
        bt_login = findViewById(R.id.bt_haveAcc)

        firebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        databaseReference = firebaseDatabase.reference

        bt_register.setOnClickListener {
            val fname = in_Fname.text.toString()
            val lname = in_Lname.text.toString()
            val email = in_Email.text.toString()
            val username = in_username.text.toString()
            val password = in_Pass.text.toString()
            val conpassword = in_ConPass.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && fname.isNotEmpty() && lname.isNotEmpty() && username.isNotEmpty()){
                if("@gmail.com" !in email){
                    Toast.makeText(this@RegisterActivity, "Enter Existing Email", Toast.LENGTH_SHORT).show()
                }
                else if (password != conpassword){
                    Toast.makeText(this@RegisterActivity, "Password Doesn't Matched", Toast.LENGTH_SHORT).show()
                }
                else if(password.length <8 ){
                    Toast.makeText(this@RegisterActivity, "Password Must Be 8 or above Characters", Toast.LENGTH_SHORT).show()
                }
                else{
                    signUp(username,"", fname, lname, email, password, 0, 0)
                }
            }
            else{
                Toast.makeText(this@RegisterActivity, "Fill Out All Fields", Toast.LENGTH_SHORT).show()
            }
        }
        bt_login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
            finish()
        }
    }

    private fun signUp(username : String,bio : String, fname : String, lname : String, email: String, password: String, sellerR: Int, buyerR: Int){
        val loading = LoadingDialog(this)
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    addUserToDatabase(username, bio, fname, lname, email, password, sellerR, buyerR, mAuth.currentUser?.uid!!)
                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    loading.startLoading()
                    val Handler = Handler()
                    Handler.postDelayed(object: Runnable{
                        override fun run() {
                            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                            finish()
                            loading.isDismiss()
                        }

                    },3000)
                }
                else{
                    Toast.makeText(this@RegisterActivity, "Email Already Exist", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun addUserToDatabase(username : String,bio : String, fname : String, lname : String, email: String, password: String, sellerR: Int, buyerR: Int, uid:String){
        databaseReference.child("accounts").child(uid).setValue(UserChat(bio, buyerR, email, fname, lname, password, sellerR, username, uid))
    }
}
