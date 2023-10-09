package com.example.studentadminapp.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentadminapp.R
import com.example.studentadminapp.databinding.ActivityShowStudentBinding
import com.example.studentadminapp.model.Student
import com.google.firebase.database.FirebaseDatabase

class ShowStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val name = intent.getStringExtra("Name")
        val email = intent.getStringExtra("Email")
        val id = intent.getStringExtra("ID")
        val standard = intent.getStringExtra("Standard")
        val password = intent.getStringExtra("Password")

        binding.etShowName.setText(name)
        binding.etShowId.text = id
        binding.etShowEmail.setText(email)
        binding.etShowStandard.text = standard
        binding.etShowPassword.setText(password)

        val dbref = FirebaseDatabase.getInstance().getReference("Student").child(id!!)

        binding.btnDelete.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.alert_title))
            builder.setMessage(getString(R.string.alert_message))
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { dialog, which ->
                dbref.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data Deleted", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Log.e("TAG", "onCreate: Failed to Delete Data")
                    }
            }
            builder.setNegativeButton("No") { dialog, which ->
                Log.d("TAG", "onCreate: Cancelled")
            }
            val alertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.show()

        }
        binding.btnUpdate.setOnClickListener {

//            val id = binding.etShowId.text.toString()
            val names = binding.etShowName.text.toString()
            val emails = binding.etShowEmail.text.toString()
            val standards = binding.etShowStandard.text.toString()
            val passwords = binding.etShowPassword.text.toString()


            val stuInfo = Student(id, names, emails, standards, passwords)
            Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show()
            dbref.setValue(stuInfo)
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.etShowStandard.setOnClickListener {
            showStandardOptionsDialog()
        }
    }

    private fun showStandardOptionsDialog() {
        val standardOptions = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Standard")
            .setItems(standardOptions) { dialog, which ->
                val selectedStandard = standardOptions[which]
                binding.etShowStandard.text = selectedStandard
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}
