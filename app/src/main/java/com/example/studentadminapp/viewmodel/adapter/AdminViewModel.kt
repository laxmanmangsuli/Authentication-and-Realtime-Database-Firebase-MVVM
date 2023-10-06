package com.example.studentadminapp.viewmodel.adapter

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentadminapp.model.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminViewModel:ViewModel() {
    private val adminReference = FirebaseDatabase.getInstance().reference.child("Admin")
    private val studentReferences = FirebaseDatabase.getInstance().reference.child("Student")

    val dataFoundLiveData = MutableLiveData<Boolean>()
    val studentListLiveData = MutableLiveData<List<Student>>()

    fun checkAdminCredentials(email: String, password: String) {
        adminReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isDataFound = false
                for (adminSnapshot in snapshot.children) {
                    val adminEmail = adminSnapshot.child("email").getValue(String::class.java)
                    val adminPassword = adminSnapshot.child("password").getValue(String::class.java)
                    if (email == adminEmail && password == adminPassword) {
                        isDataFound = true
                        break
                    }
                }
                if (isDataFound) {
                    dataFoundLiveData.postValue(true)
                } else {
                    dataFoundLiveData.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
    fun fetchStudentData() {
        studentReferences.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val studentList = mutableListOf<Student>()
                for (studentSnapshot in snapshot.children) {
                    val name = studentSnapshot.child("name").getValue(String::class.java)
                    val id = studentSnapshot.child("id").getValue(String::class.java)
                    val email = studentSnapshot.child("email").getValue(String::class.java)
                    val standard = studentSnapshot.child("standard").getValue(String::class.java)
                    val password = studentSnapshot.child("password").getValue(String::class.java)
                    val student = Student(id = id, name = name!!, email = email!!, standard = standard!!, password = password!!)
                    studentList.add(student)
                }
                studentListLiveData.postValue(studentList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled:${error.message} ")
            }
        })
    }
}