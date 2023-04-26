package com.example.roomdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.roomdb.database.AppDatabase
import com.example.roomdb.databinding.ActivityMainBinding
import com.example.roomdb.entities.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var appDb: AppDatabase
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appDb = AppDatabase.getDatabase(this)
        binding.btnWriteData.setOnClickListener {
            writeData()
        }

        binding.btnReadData.setOnClickListener {
            readData()
        }

        binding.btnDeleteAll.setOnClickListener {
            GlobalScope.launch {
                appDb.studentDao().deleteAll()
            }
        }
    }

    private fun writeData() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val rollNo = binding.etRollNo.text.toString()

        if (firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            rollNo.isNotEmpty()
        ) {
            val student = Student(null, firstName, lastName, rollNo.toInt())
            GlobalScope.launch(Dispatchers.IO){
                appDb.studentDao().insert(student)
            }

            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etRollNo.text.clear()

            Toast.makeText(this@MainActivity, "Successfully written", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this@MainActivity, "Please enter all data", Toast.LENGTH_LONG).show()
        }
    }

    private fun readData(){
        val rollNo = binding.etRollNoRead.text.toString()
        if(rollNo.isNotEmpty()){
            lateinit var student: Student
            GlobalScope.launch{
                student = appDb.studentDao().findByRoll(rollNo.toInt())
                displayData(student)
            }
        }
    }

    private suspend fun displayData(student: Student) {
        withContext(Dispatchers.Main){
            binding.tvFirstName.text = student.firstName
            binding.tvLastName.text = student.lastName
            binding.tvRollNo.text = student.rollNo.toString()
        }
    }


}