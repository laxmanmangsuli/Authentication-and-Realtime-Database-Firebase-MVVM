package com.example.studentadminapp.view.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.studentadminapp.view.MainActivity
import com.example.studentadminapp.databinding.ActivityAdminSignUpBinding
import com.example.studentadminapp.model.Admin
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminSignUpActivity : AppCompatActivity() {
    lateinit var dbref:DatabaseReference
    private lateinit var binding:ActivityAdminSignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbref = FirebaseDatabase.getInstance().getReference("Admin")
//        val database = FirebaseDatabase.getInstance().reference
        binding.btnAdminAdd.setOnClickListener {

            val name = binding.etAdminName.text.toString()
            val email = binding.etAdminEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val id=dbref.push().key

            val addStudent =
                Admin(id,name, email, password)

//            val studentReference = database.child("Admin").push()
            dbref.child(id!!).setValue(addStudent)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Added Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener { error ->

                    Log.e("TAG", "Error saving data: ${error.message}")
                }
        }

    }
}