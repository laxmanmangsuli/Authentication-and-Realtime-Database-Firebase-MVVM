package com.example.studentadminapp.viewmodel.adapter

import android.content.Context
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentadminapp.R
import com.example.studentadminapp.model.Student
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class GoogleSignInViewModel : ViewModel() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    suspend fun signInWithGoogle(idToken: String): Result<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            if (authResult.user != null) {
                Result.success(true)
            } else {
                Result.failure(Exception("Sign-in failed"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}