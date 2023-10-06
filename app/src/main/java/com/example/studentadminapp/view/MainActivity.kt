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


//        val button = binding.btnSelect
//        button.setOnClickListener {
//            val popupMenu = PopupMenu(this, button)
//            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
//            popupMenu.setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.student -> {
//                        binding.llStudent.visibility = View.VISIBLE
//                        binding.signUp.setOnClickListener {
//                            val intent = Intent(this, StudentSignUpActivity::class.java)
//                            startActivity(intent)
//                        }
//                        val database = FirebaseDatabase.getInstance().reference
//                        val studentReference = database.child("Student")
//                        binding.btnSubmit.setOnClickListener {
//                            val emails = binding.email.text.toString()
//                            val passwords = binding.password.text.toString()
//                            studentReference.addValueEventListener(object : ValueEventListener {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//                                    val studentList = mutableListOf<Student>()
//                                    var isDataFound = false
//
//                                    for (studentSnapshot in snapshot.children) {
//                                        val name = studentSnapshot.child("name")
//                                            .getValue(String::class.java)
//                                        val email = studentSnapshot.child("email")
//                                            .getValue(String::class.java)
//                                        val standard = studentSnapshot.child("standard")
//                                            .getValue(String::class.java)
//                                        val password = studentSnapshot.child("password")
//                                            .getValue(String::class.java)
//                                        if (emails == email && passwords == password) {
//                                            val student = Student(
//                                                name!!,
//                                                email,
//                                                standard!!,
//                                                password
//                                            )
//                                            studentList.add(student)
//                                            isDataFound = true
//                                            break
//                                        }
//                                    }
//                                    if (isDataFound) {
//                                        binding.tvName.text = studentList[0].name
//                                        binding.tvEmail.text = studentList[0].email
//                                        binding.tvStandard.text = studentList[0].standard
//                                        binding.tvPassword.text = studentList[0].password
//                                    } else {
//                                        binding.tvName.text = ""
//                                        binding.tvEmail.text = ""
//                                        binding.tvStandard.text = ""
//                                        binding.tvPassword.text = ""
//                                        Toast.makeText(
//                                            this@MainActivity,
//                                            "Data Not Found",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    Log.e("TAG", "Error retrieving data: ${error.message}")
//                                }
//                            })
//                        }
//
//                        binding.btnSelect.visibility = View.GONE
//                        binding.btnSignIn.visibility = View.GONE
//                    }

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


//                        binding.btnAdSubmit.setOnClickListener {
//                            binding.recycler.visibility = View.VISIBLE
//                            adminReference.addValueEventListener(object : ValueEventListener {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//                                    val emails = binding.adEmail.text.toString()
//                                    val passwords = binding.adPassword.text.toString()
//                                    var isDataFound = false
//                                    for (studentSnapshot in snapshot.children) {
//                                        val email = studentSnapshot.child("email")
//                                            .getValue(String::class.java)
//                                        val password = studentSnapshot.child("password")
//                                            .getValue(String::class.java)
//                                        if (emails == email && passwords == password) {
//                                            isDataFound = true
//                                            break
//                                        }
//                                    }
//                                    val databases = FirebaseDatabase.getInstance().reference
//                                    val studentReferences = databases.child("Student")
//                                    if (isDataFound) {
//                                        studentReferences.addValueEventListener(object :
//                                            ValueEventListener {
//                                            override fun onDataChange(snapshot: DataSnapshot) {
//                                                for (studentSnapshot in snapshot.children) {
//                                                    val name = studentSnapshot.child("name")
//                                                        .getValue(String::class.java)
//                                                    val id = studentSnapshot.child("id")
//                                                        .getValue(String::class.java)
//                                                    val email = studentSnapshot.child("email")
//                                                        .getValue(String::class.java)
//                                                    val standard = studentSnapshot.child("standard")
//                                                        .getValue(String::class.java)
//                                                    val password = studentSnapshot.child("password")
//                                                        .getValue(String::class.java)
//                                                    val student = Student(
//                                                        id = id,
//                                                        name = "$name",
//                                                        email = "$email",
//                                                        standard = "$standard",
//                                                        password = "$password"
//
//
//                                                    )
//                                                    data.add(student)
//                                                    studentAdapter =
//                                                        StudentAdapter(this@MainActivity, data)
//                                                    val recyclerView = binding.recycler
//                                                    recyclerView.layoutManager =
//                                                        LinearLayoutManager(this@MainActivity)
//                                                    val adapter =
//                                                        StudentAdapter(this@MainActivity, data)
//                                                    recyclerView.adapter = adapter
//                                                    binding.adEmail.text.clear()
//                                                    binding.adPassword.text.clear()
//                                                    adapter.notifyDataSetChanged()
//                                                }
//                                            }
//
//                                            override fun onCancelled(error: DatabaseError) {
//                                                Log.e(
//                                                    "TAG",
//                                                    "Error retrieving data: ${error.message}"
//                                                )
//                                            }
//                                        })
//                                    } else {
//                                        Toast.makeText(
//                                            this@MainActivity,
//                                            "Data Not Found",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    Log.e("TAG", "Error retrieving data: ${error.message}")
//                                }
//                            })
//                        }
//                        binding.btnSelect.visibility = View.GONE
//                        binding.btnSignIn.visibility = View.GONE
//                    }
//                }
//                true
//            }
//            popupMenu.show()
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


//        FirebaseApp.initializeApp(this)
//
//        val gsi = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInclient = GoogleSignIn.getClient(this,gsi)
//        firebaseAuth =FirebaseAuth.getInstance()
//        binding.btnSignIn.setOnClickListener {
//                view: View? ->
//            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
//            signInGoogle()
//        }
//    private fun signInGoogle() {
//        val signInIntent =googleSignInclient.signInIntent
//        launcher.launch(signInIntent)
//    }
//    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//            result->
//        if(result.resultCode== Activity.RESULT_OK){
//            val task =GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            handleResult(task)
//        }
//    }
//    private fun handleResult(task : Task<GoogleSignInAccount>) {
//        if (task.isSuccessful){
//            val account :GoogleSignInAccount? =task.result
//            if (account!=null){
//                updateUi(account)
//            }
//
//        }else{
//            Toast.makeText(this , task.exception.toString() , Toast.LENGTH_SHORT).show()
//        }
//
//    }
//       private fun updateUi(account : GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
//        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
//            if (it.isSuccessful){
//                val intent = Intent(this, DashboardActivity::class.java)
//                intent.putExtra("Email",account.email)
//                intent.putExtra("Name",account.displayName)
//                startActivity(intent)
//            }else{
//                Toast.makeText(this , it.exception.toString() , Toast.LENGTH_SHORT).show()
//
//            }
//        }
//    }