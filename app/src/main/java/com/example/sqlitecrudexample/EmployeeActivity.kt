package com.example.sqlitecrudexample

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.assignment1.EmployeeAdapter

class EmployeeActivity : AppCompatActivity() {
    public val DATABASE_NAME = "myemployeedatabase"
    lateinit var employeeList:ArrayList<Employee>
    lateinit var mDatabase: SQLiteDatabase
    lateinit var listViewEmployee: ListView
    lateinit var adapter: EmployeeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        listViewEmployee = findViewById(R.id.listViewEmployees)
        employeeList = ArrayList<Employee>()

        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE,null)

        showEmployeesFromDatabase()
    }

    fun showEmployeesFromDatabase(){
        var cursorEmployees = mDatabase.rawQuery("SELECT * FROM employees",null)
        if(cursorEmployees.moveToFirst()){
            do {
                employeeList.add(
                    Employee(
                    cursorEmployees.getInt(0),
                        cursorEmployees.getString(1),
                        cursorEmployees.getString(2),
                        cursorEmployees.getString(3),
                        cursorEmployees.getDouble(4)
                )
                )
            }
            while (cursorEmployees.moveToNext())
        }
        cursorEmployees.close()
        adapter = EmployeeAdapter(this,employeeList,mDatabase)
        listViewEmployee.adapter = adapter
    }
}