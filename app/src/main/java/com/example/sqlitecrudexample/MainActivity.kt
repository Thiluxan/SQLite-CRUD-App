package com.example.sqlitecrudexample

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    public val DATABASE_NAME = "myemployeedatabase"

    lateinit var textViewViewEmployees : TextView
    lateinit var editTextName: EditText
    lateinit var editTextSalary : EditText
    lateinit var spinnerDepartment: Spinner
    lateinit var mDatabase : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewViewEmployees = findViewById(R.id.textViewViewEmployees)
        editTextName = findViewById(R.id.editTextName)
        editTextSalary = findViewById(R.id.editTextSalary)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)

        findViewById<Button>(R.id.buttonAddEmployee).setOnClickListener{
            addEmployee()
        }

        textViewViewEmployees.setOnClickListener {
            startActivity(Intent(this, EmployeeActivity::class.java))
        }
        mDatabase = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null)
        createEmployeeTable()
    }

    private fun inputsAreCorrect(name: String, salary: String): Boolean {
        if (name.isEmpty()) {
            editTextName.error = "Please enter a name"
            editTextName.requestFocus()
            return false
        }
        if (salary.isEmpty() || salary.toInt() <= 0) {
            editTextSalary.error = "Please enter salary"
            editTextSalary.requestFocus()
            return false
        }
        return true
    }

    private fun addEmployee(){
        var name:String = editTextName.text.toString().trim()
        var salary:String = editTextSalary.text.toString().trim()
        var dept = spinnerDepartment.selectedItem.toString()

        var calendar = Calendar.getInstance()
        var simpleDateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        var joiningDate = simpleDateFormat.format(calendar.time)

        if(inputsAreCorrect(name,salary)){
            val insertSQL = """
                INSERT INTO employees 
                (name, department, joiningdate, salary)
                VALUES 
                (?, ?, ?, ?);
                """.trimIndent()
            mDatabase.execSQL(insertSQL, arrayOf(name, dept, joiningDate, salary))
            Toast.makeText(this,"Employee Added Successfully",Toast.LENGTH_SHORT).show()
        }
    }

    private fun createEmployeeTable() {
        mDatabase.execSQL(
            """CREATE TABLE IF NOT EXISTS employees (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name varchar(200) NOT NULL,
                    department varchar(200) NOT NULL,
                    joiningdate datetime NOT NULL,
                    salary double NOT NULL
                );"""
        )
    }
}


