package com.example.studentadminapp.viewmodel.adapter

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentadminapp.model.Student
import com.google.firebase.database.*

class StudentViewModel : ViewModel() {
    private val studentReference = FirebaseDatabase.getInstance().reference.child("Student")
    val studentDataLiveData = MutableLiveData<List<Student>>()
    val dataFoundLiveData = MutableLiveData<Boolean>()
    fun checkStudentCredentials(email: String, password: String) {
        studentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val studentList = mutableListOf<Student>()
                var isDataFound = false
                for (studentSnapshot in snapshot.children) {
                    val id = studentSnapshot.child("id").getValue(String::class.java)
                    val name = studentSnapshot.child("name").getValue(String::class.java)
                    val studentEmail = studentSnapshot.child("email").getValue(String::class.java)
                    val standard = studentSnapshot.child("standard").getValue(String::class.java)
                    val studentPassword = studentSnapshot.child("password").getValue(String::class.java)

                    if (email == studentEmail && password == studentPassword) {
                        val student = Student(id ?:"",name ?: "", studentEmail ?: "", standard ?: "", studentPassword ?: "")
                        studentList.add(student)
                        isDataFound = true
                        break
                    }
                }

                if (isDataFound) {
                    studentDataLiveData.postValue(studentList)
                } else {
                    dataFoundLiveData.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled:${error.message} ")
            }
        })
    }
}
