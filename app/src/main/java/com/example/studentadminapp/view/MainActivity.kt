package com.example.studentadminapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentadminapp.R
import com.example.studentadminapp.databinding.ActivityMainBinding
import com.example.studentadminapp.model.Student
import com.example.studentadminapp.view.signup.AdminSignUpActivity
import com.example.studentadminapp.view.signup.StudentSignUpActivity
import com.example.studentadminapp.viewmodel.adapter.AdminViewModel
import com.example.studentadminapp.viewmodel.adapter.GoogleSignInViewModel
import com.example.studentadminapp.viewmodel.adapter.StudentAdapter
import com.example.studentadminapp.viewmodel.adapter.StudentViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var googleSignInViewModel: GoogleSignInViewModel
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var binding: ActivityMainBinding
//    private lateinit var studentAdapter: StudentAdapter
    private val  data = ArrayList<Student>()
    private val RC_SIGN_IN = 123
//    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        googleSignInViewModel = ViewModelProvider(this)[GoogleSignInViewModel::class.java]
        adminViewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        binding.btnSignIn.setOnClickListener {
            signInWithGoogle()
        }
        val recyclerView = binding.recycler
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        val adapter = StudentAdapter(this@MainActivity, data)
        recyclerView.adapter = adapter
        binding.btnSelect.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.btnSelect)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.student -> {
                        binding.llStudent.visibility = View.VISIBLE
                        binding.signUp.setOnClickListener {
                            val intent = Intent(this, StudentSignUpActivity::class.java)
                            startActivity(intent)
                        }
                        binding.btnSubmit.setOnClickListener {
                            val email = binding.email.text.toString()
                            val password = binding.password.text.toString()

                            studentViewModel.checkStudentCredentials(email, password)
                        }

                        studentViewModel.studentDataLiveData.observe(this) { studentList ->
                            if (studentList.isNotEmpty()) {
                                binding.tvName.text = studentList[0].name
                                binding.tvEmail.text = studentList[0].email
                                binding.tvStandard.text = studentList[0].standard
                                binding.tvPassword.text = studentList[0].password
                            } else {
                                binding.tvName.text = ""
                                binding.tvEmail.text = ""
                                binding.tvStandard.text = ""
                                binding.tvPassword.text = ""
                            }
                        }
                        studentViewModel.dataFoundLiveData.observe(this) { isDataFound ->
                            if (!isDataFound) {
                                Toast.makeText(this@MainActivity, "Data Not Found", Toast.LENGTH_SHORT).show()
                            }
                        }

                        binding.btnSelect.visibility = View.GONE
                        binding.btnSignIn.visibility = View.GONE
                    }

                    R.id.admin -> {
                        binding.lLAdmin.visibility = View.VISIBLE
                        binding.adSignUp.setOnClickListener {
                            val intent = Intent(this, AdminSignUpActivity::class.java)
                            startActivity(intent)
                        }
                        binding.btnAdSubmit.setOnClickListener {
                            val email = binding.adEmail.text.toString()
                            val password = binding.adPassword.text.toString()

                            adminViewModel.checkAdminCredentials(email, password)
                        }

                        adminViewModel.dataFoundLiveData.observe(this) { isDataFound ->
                            if (isDataFound) {
                                adminViewModel.fetchStudentData()
                            } else {
                                Toast.makeText(this@MainActivity, "Data Not Found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        adminViewModel.studentListLiveData.observe(this) { studentList ->
                            val recyclerView = binding.recycler
                            recyclerView.layoutManager = LinearLayoutManager(this)
                            val adapter = StudentAdapter(this, studentList)
                            recyclerView.adapter = adapter
                            binding.adEmail.text.clear()
                            binding.adPassword.text.clear()
                            adapter.notifyDataSetChanged()
                        }
                        binding.btnSelect.visibility = View.GONE
                        binding.btnSignIn.visibility = View.GONE
                    }
                }
                true
            }
            popupMenu.show()

        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            Log.d("MainActivity", "User is signed in. Starting DashboardActivity.")
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = googleSignInViewModel.signInWithGoogle(idToken)
                        if (result.isSuccess) {
                            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("Email", account.email)
                            editor.apply()
                            startActivity(intent)

                        } else {
                            Log.e("TAG", "onActivityResult:${result.isFailure} ")
                        }
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

}
