package com.petra.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.petra.chatapp.Utils
import com.petra.chatapp.modal.Users

class UsersRepo {
    private var firestore = FirebaseFirestore.getInstance()

    fun getUsers():LiveData<List<Users>>{
        val users = MutableLiveData<List<Users>>()
        firestore.collection("users").addSnapshotListener{
            snapshot, exception -> if (exception != null){
                return@addSnapshotListener

        }
            val userList = mutableListOf<Users>()
            snapshot?.documents?.forEach{document ->
                val user = document.toObject(Users::class.java)
                if (user!!.userid != Utils.getUiLoggedIn()){
                    user.let {
                        userList.add(it)
                    }
                }
                users.value = userList

            }
        }
        return users
    }
}