@file:Suppress("DEPRECATION")

package com.petra.chatapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.petra.chatapp.MainActivity
import com.petra.chatapp.R
import com.petra.chatapp.databinding.ActivitySignInBinding

class Sign_In : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialogSignIn: ProgressDialog
    private lateinit var signInBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))

        }

        progressDialogSignIn = ProgressDialog(this)


        signInBinding.signInTextToSignUp.setOnClickListener{
            startActivity(Intent(this, Sign_Up::class.java))
        }

        signInBinding.loginButton.setOnClickListener{
            email = signInBinding.loginetemail.text.toString()
            password = signInBinding.loginetpassword.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Enter Email & Password", Toast.LENGTH_SHORT).show()
            }else{
                signIn(email, password)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Signing In")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful){
                progressDialogSignIn.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                progressDialogSignIn.dismiss()
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{exception->
            when(exception){
                is FirebaseAuthInvalidCredentialsException -> {
                    Toast.makeText(this, "Invalid Email or Password ", Toast.LENGTH_SHORT).show()
                } else -> {
                    Toast.makeText(this, "Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        progressDialogSignIn.dismiss()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialogSignIn.dismiss()
    }
}