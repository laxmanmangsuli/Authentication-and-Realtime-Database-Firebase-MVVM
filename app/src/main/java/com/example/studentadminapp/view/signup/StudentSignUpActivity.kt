package com.example.studentadminapp.view.signup

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentadminapp.databinding.ActivityStudentSignUpBinding
import com.example.studentadminapp.model.Student
import com.example.studentadminapp.view.MainActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class StudentSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentSignUpBinding
    private lateinit var dbref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbref = FirebaseDatabase.getInstance().getReference("Student")
        val database = FirebaseDatabase.getInstance().reference
        binding.btnAdd.setOnClickListener {

            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val standard = binding.etStandard.text.toString()
            val password = binding.etPassword.text.toString()


            val id = dbref.push().key
            val addStudent =
                Student(id, name, email, standard, password)
            dbref.child(id!!).setValue(addStudent)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener { error ->
                    Log.e("TAG", "Error saving data: ${error.message}")
                }
        }

        binding.etStandard.setOnClickListener {
            showStandardOptionsDialog()
        }
    }

    private fun showStandardOptionsDialog() {
        val standardOptions = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Standard")
            .setItems(standardOptions) { dialog, which ->
                val selectedStandard = standardOptions[which]
                binding.etStandard.setText(selectedStandard)
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

}


