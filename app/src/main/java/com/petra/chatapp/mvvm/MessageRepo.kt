package com.petra.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.petra.chatapp.Utils
import com.petra.chatapp.modal.Messages

class MessageRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(friendid: String): LiveData<List<Messages>> {
        val uniqueid = listOf(Utils.getUiLoggedIn(), friendid).sorted()
        val messages = MutableLiveData<List<Messages>>()
        uniqueid.joinToString (separator = "")

        firestore.collection("Messages").document(uniqueid.toString()).collection("chats")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val messageList = mutableListOf<Messages>()
                if (!value!!.isEmpty) {
                    value.documents.forEach{
                        document ->
                        val messageModal = document.toObject(Messages::class.java)

                        if (messageModal!!.sender.equals(Utils.getUiLoggedIn()) && messageModal.receiver.equals(friendid) ||
                            messageModal.sender.equals(friendid) && messageModal.receiver.equals(Utils.getUiLoggedIn())
                        ) {
                            messageModal.let { it ->
                                messageList.add(it)
                            }
                        }
                    }
                    messages.value = messageList

                }
    }
        return messages
    }

}
