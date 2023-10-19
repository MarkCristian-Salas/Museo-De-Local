package com.example.museodelokal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedAccountsActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sort: Spinner
    private lateinit var order: Spinner
    private lateinit var back: ImageView
    private lateinit var update : Button
    private lateinit var count : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_accounts)
        databaseHelper = DatabaseHelper(this)
        back = findViewById(R.id.back_button)
        sort = findViewById(R.id.sortby)
        order = findViewById(R.id.orderby)

        update = findViewById(R.id.bt_Update2)
        count = findViewById(R.id.bt_Count)

        val tvEmail0 = findViewById<TextView>(R.id.email_0)
        val tvEmail1 = findViewById<TextView>(R.id.email_1)
        val tvEmail2 = findViewById<TextView>(R.id.email_2)
        val tvEmail3 = findViewById<TextView>(R.id.email_3)
        val tvEmail4 = findViewById<TextView>(R.id.email_4)
        val tvEmail5 = findViewById<TextView>(R.id.email_5)

        val tvFname0 = findViewById<TextView>(R.id.fname_0)
        val tvFname1 = findViewById<TextView>(R.id.fname_1)
        val tvFname2 = findViewById<TextView>(R.id.fname_2)
        val tvFname3 = findViewById<TextView>(R.id.fname_3)
        val tvFname4 = findViewById<TextView>(R.id.fname_4)
        val tvFname5 = findViewById<TextView>(R.id.fname_5)

        val tvLname0 = findViewById<TextView>(R.id.lname_0)
        val tvLname1 = findViewById<TextView>(R.id.lname_1)
        val tvLname2 = findViewById<TextView>(R.id.lname_2)
        val tvLname3 = findViewById<TextView>(R.id.lname_3)
        val tvLname4 = findViewById<TextView>(R.id.lname_4)
        val tvLname5 = findViewById<TextView>(R.id.lname_5)

        val tvUsername0 = findViewById<TextView>(R.id.username_0)
        val tvUsername1 = findViewById<TextView>(R.id.username_1)
        val tvUsername2 = findViewById<TextView>(R.id.username_2)
        val tvUsername3 = findViewById<TextView>(R.id.username_3)
        val tvUsername4 = findViewById<TextView>(R.id.username_4)
        val tvUsername5 = findViewById<TextView>(R.id.username_5)

        val sortOption = arrayOf("email", "fname", "lname", "username")
        val orderOption = arrayOf("ASC", "DESC")

        var SORTBY = ""
        var ORDERBY = ""

        back.setOnClickListener {

            back.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        sort.adapter = ArrayAdapter<String> (this,android.R.layout.simple_list_item_1, sortOption)
        order.adapter = ArrayAdapter<String> (this,android.R.layout.simple_list_item_1, orderOption)

        sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                SORTBY = sortOption[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        order.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                ORDERBY = orderOption[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        val emailList=ArrayList<String>()
        val fnameList=ArrayList<String>()
        val lnameList=ArrayList<String>()
        val usernameList=ArrayList<String>()
        update.setOnClickListener {
            val helper = DatabaseHelper(applicationContext)
            val db = helper.readableDatabase
            val rs = db.rawQuery("SELECT * FROM SavedAccounts ORDER BY $SORTBY $ORDERBY",null)
            while(rs.moveToNext()) {
                emailList.add(rs.getString(0).toString())
                fnameList.add(rs.getString(2).toString().uppercase())
                lnameList.add(rs.getString(3).toString().uppercase())
                usernameList.add(rs.getString(4).toString())
            }
            val email_array: Array<String> = emailList.toTypedArray()
            val fname_array: Array<String> = fnameList.toTypedArray()
            val lname_array: Array<String> = lnameList.toTypedArray()
            val username_array: Array<String> = usernameList.toTypedArray()

            for (x in 0..(emailList.size-1)){
                if(x == 0){
                    tvEmail0.text = email_array[0]
                    tvFname0.text = fname_array[0]
                    tvLname0.text = lname_array[0]
                    tvUsername0.text = username_array[0]
                }
                if(x == 1){
                    tvEmail1.text = email_array[1]
                    tvFname1.text = fname_array[1]
                    tvLname1.text = lname_array[1]
                    tvUsername1.text = username_array[1]
                }
                if(x == 2){
                    tvEmail2.text = email_array[2]
                    tvFname2.text = fname_array[2]
                    tvLname2.text = lname_array[2]
                    tvUsername2.text = username_array[2]
                }
                if(x == 3){
                    tvEmail3.text = email_array[3]
                    tvFname3.text = fname_array[3]
                    tvLname3.text = lname_array[3]
                    tvUsername3.text = username_array[3]
                }
                if(x == 4){
                    tvEmail4.text = email_array[4]
                    tvFname4.text = fname_array[4]
                    tvLname4.text = lname_array[4]
                    tvUsername4.text = username_array[4]
                }
                if(x == 5){
                    tvEmail5.text = email_array[5]
                    tvFname5.text = fname_array[5]
                    tvLname5.text = lname_array[5]
                    tvUsername5.text = username_array[5]
                }
            }
            emailList.clear()
            fnameList.clear()
            lnameList.clear()
            usernameList.clear()

        }
        count.setOnClickListener {
            Toast.makeText(this, emailList.size, Toast.LENGTH_SHORT).show()
        }


    }

}