
package com.example.assignment1

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sqlitecrudexample.Employee
import com.example.sqlitecrudexample.R


class EmployeeAdapter(private val context: Activity, private val employeeList: ArrayList<Employee>, private val mDatabase: SQLiteDatabase)
    : ArrayAdapter<Employee>(context, R.layout.list_layout_employee, employeeList) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater!!.inflate(R.layout.list_layout_employee, null, true)

        var employee = employeeList[position]

        var textViewName = rowView.findViewById<TextView>(R.id.textViewName)
        var textViewDept = rowView.findViewById<TextView>(R.id.textViewDepartment)
        var textViewSalary = rowView.findViewById<TextView>(R.id.textViewSalary)
        var textViewJoiningDate = rowView.findViewById<TextView>(R.id.textViewJoiningDate)

        textViewName.text = employee.name
        textViewDept.text = employee.dept
        textViewSalary.text = employee.salary.toString()
        textViewJoiningDate.text = employee.joiningDate

        var buttonEdit = rowView.findViewById<Button>(R.id.buttonEditEmployee)
        var buttonDelete = rowView.findViewById<Button>(R.id.buttonDeleteEmployee)

        buttonEdit.setOnClickListener { updateEmployee(employee) }

        buttonDelete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure?")
            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val sql = "DELETE FROM employees WHERE id = ?"
                    mDatabase.execSQL(sql, arrayOf(employee.id))
                    reloadEmployeesFromDatabase()
                })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialogInterface, i -> })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        return rowView
    }

    private fun updateEmployee(employee: Employee) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.update_employee, null)
        builder.setView(view)

        val editTextName = view.findViewById<EditText>(R.id.editTextName)
        val editTextSalary = view.findViewById<EditText>(R.id.editTextSalary)
        val spinnerDepartment = view.findViewById<Spinner>(R.id.spinnerDepartment)

        editTextName.setText(employee.name)
        editTextSalary.setText(employee.salary.toString())

        val dialog = builder.create()
        dialog.show()
        view.findViewById<View>(R.id.buttonUpdateEmployee)
            .setOnClickListener(View.OnClickListener {
                val name = editTextName.text.toString().trim { it <= ' ' }
                val salary =
                    editTextSalary.text.toString().trim { it <= ' ' }
                val dept = spinnerDepartment.selectedItem.toString()
                if (name.isEmpty()) {
                    editTextName.error = "Name can't be blank"
                    editTextName.requestFocus()
                    return@OnClickListener
                }
                if (salary.isEmpty()) {
                    editTextSalary.error = "Salary can't be blank"
                    editTextSalary.requestFocus()
                    return@OnClickListener
                }
                val sql = """
                    UPDATE employees 
                    SET name = ?, 
                    department = ?, 
                    salary = ? 
                    WHERE id = ?;
                    
                    """.trimIndent()
                mDatabase.execSQL(
                    sql,
                    arrayOf(name, dept, salary, employee.id.toString())
                )
                Toast.makeText(context, "Employee Updated", Toast.LENGTH_SHORT).show()
                reloadEmployeesFromDatabase()
                dialog.dismiss()
            })
    }

    private fun reloadEmployeesFromDatabase() {
        val cursorEmployees: Cursor = mDatabase.rawQuery("SELECT * FROM employees", null)
        if (cursorEmployees.moveToFirst()) {
            employeeList.clear()
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
            } while (cursorEmployees.moveToNext())
        }
        cursorEmployees.close()
        notifyDataSetChanged()
    }

}