@file:Suppress("DEPRECATION")

package com.petra.chatapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.petra.chatapp.R
import com.petra.chatapp.databinding.ActivitySignUpBinding

class Sign_Up : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var signUpAuth: FirebaseAuth
    private lateinit var name:String
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var signUpPd: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        firestore = FirebaseFirestore.getInstance()
        signUpAuth = FirebaseAuth.getInstance()
        signUpPd = ProgressDialog(this)

        signUpBinding.signUpTextToSignIn.setOnClickListener{
            startActivity(Intent(this, Sign_In::class.java))
        }

        signUpBinding.signUpBtn.setOnClickListener {
            name = signUpBinding.signUpEtName.text.toString()
            email = signUpBinding.signUpEmail.text.toString()
            password = signUpBinding.signUpPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
            } else {
                signUp(name, email, password)
            }
        }

    }

    private fun signUp(name: String, email: String, password: String) {
        signUpPd.show()
        signUpPd.setMessage("Signing Up")
        signUpAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = signUpAuth.currentUser
                val hashMap = hashMapOf("userid" to user!!.uid, "username" to name, "useremail" to email,
                    "status" to "Online", "imageUrl" to "https://png.pngtree.com/png-vector/20210921/ourlarge/pngtree-flat-people-profile-icon-png-png-image_3947764.png")

                firestore.collection("users").document(user.uid).set(hashMap)
                signUpPd.dismiss()
                startActivity(Intent(this, Sign_In::class.java))

            }
        }
    }
}