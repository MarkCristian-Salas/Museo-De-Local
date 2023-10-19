package com.example.museodelokal

import android.accounts.Account
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.CloudMediaProviderContract.AlbumColumns
import com.google.firebase.database.snapshot.BooleanNode
import java.lang.Exception

class DatabaseHelper (private val context: Context):
            SQLiteOpenHelper(context, DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SavedAccounts.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "SavedAccounts"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_FNAME = "fname"
        private const val COLUMN_LNAME = "lname"
        private const val COLUMN_USERNAME = "username"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME(" +
                "$COLUMN_EMAIL TEXT PRIMARY KEY, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_FNAME TEXT , " +
                "$COLUMN_LNAME TEXT , " +
                "$COLUMN_USERNAME TEXT )")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }


    fun insertUser(user:User){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_FNAME, user.fname)
            put(COLUMN_LNAME, user.lname)
            put(COLUMN_USERNAME, user.username)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }



    fun deleteData(Phone:String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_EMAIL=?", arrayOf(Phone))
        db.close()
    }

    fun updateData( NewPassword: String, Email: String){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_PASSWORD, NewPassword)
        db.update(TABLE_NAME, values, "$COLUMN_EMAIL=?" , arrayOf(Email))
        db.close()
    }
    fun updateUsername( NewUsername: String, Email: String){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_USERNAME, NewUsername)
        db.update(TABLE_NAME, values, "$COLUMN_EMAIL=?" , arrayOf(Email))
        db.close()
    }
}