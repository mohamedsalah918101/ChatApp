package com.petra.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.petra.chatapp.Utils
import com.petra.chatapp.modal.RecentChats

class ChatListRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatList(): LiveData<List<RecentChats>> {
        val mainChatList = MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversation${Utils.getUiLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val chatlist = mutableListOf<RecentChats>()
                value?.forEach { document ->
                    val recentmodel = document.toObject(RecentChats::class.java)
                    if (recentmodel.sender.equals(Utils.getUiLoggedIn())) {
                        recentmodel.let {
                            chatlist.add(it)
                        }
                    }
                }
                mainChatList.value = chatlist
            }
        return mainChatList

    }
}