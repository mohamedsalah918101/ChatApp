package com.petra.chatapp.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.petra.chatapp.MyApplication
import com.petra.chatapp.SharedPrefs
import com.petra.chatapp.Utils
import com.petra.chatapp.modal.Messages
import com.petra.chatapp.modal.RecentChats
import com.petra.chatapp.modal.Users
import com.petra.chatapp.notifications.entity.NotificationData
import com.petra.chatapp.notifications.entity.PushNotification
import com.petra.chatapp.notifications.entity.Token
import com.petra.chatapp.notifications.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()
    val messagesRepo = MessageRepo()
    val recentChatRepo = ChatListRepo()

    var token: String? = null

    init {
        getCurrentUser()
        getRecentChat()
    }

    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        firestore.collection("users").document(Utils.getUiLoggedIn())
            .addSnapshotListener { value, error ->
                if (value!!.exists()) {
                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!
                    val mySharedPrefs = SharedPrefs(context)
                    mySharedPrefs.setValue("username", users.username)

                }
            }
    }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val context = MyApplication.instance.applicationContext
            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )
            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")
            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mySharedPrefs = SharedPrefs(context)
            mySharedPrefs.setValue("friendid", receiver)
            mySharedPrefs.setValue("chatroomid", uniqueId.toString())
            mySharedPrefs.setValue("friendname", friendnamesplit)
            mySharedPrefs.setValue("friendimage", friendimage)

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { task ->
                    val hashMapForRecent = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUiLoggedIn(),
                        "message" to message.value!!,
                        "friendimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUiLoggedIn()}")
                        .document("receiver")
                        .set(hashMapForRecent)
                    firestore.collection("Conversation${receiver}").document(Utils.getUiLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )

                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, error ->
                            if (value != null && value.exists()) {
                                val tokenObject = value.toObject(Token::class.java)
                                token = tokenObject?.token!!
                                val loggedUserName =
                                    mySharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]
                                if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(
                                            loggedUserName,
                                            message.value!!
                                        ), token!!
                                    ).also {
                                        sendNotification(it)
                                    }
                                } else {
                                    Log.e("VIEWMODEL", "NO TOKEN, NO NOTIFICATION")
                                }
                            }

                            Log.e("ViewModel", token.toString())


                            if (task.isSuccessful) {
                                message.value = ""
                            }

                        }

                }
        }

    private fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d("VIEWMODEL", "Notification sent successfully")
            } else {
                Log.e("VIEWMODEL", "Notification failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("VIEWMODELERROR", e.toString())
        }
    }

    fun getMessages(friendid: String): LiveData<List<Messages>> {
        return messagesRepo.getMessages(friendid)
    }

    fun getRecentChat(): LiveData<List<RecentChats>> {
        return recentChatRepo.getAllChatList()
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        val hashMapUser = hashMapOf<String, Any>(
            "username" to name.value!!,
            "imageUrl" to imageUrl.value!!
        )
        firestore.collection("users").document(Utils.getUiLoggedIn()).update(hashMapUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                }

            }
        val mySharedPrefs = SharedPrefs(context)
        val friendid = mySharedPrefs.getValue("friendid")
        val hashMapUpdate = hashMapOf<String, Any>(
            "friendsimage" to imageUrl.value!!,
            "name" to name.value!!,
            "person" to name.value!!
        )
        if (friendid != null) {
            firestore.collection("Conversation${friendid}").document(Utils.getUiLoggedIn())
                .update(hashMapUpdate)
            firestore.collection("Conversation${Utils.getUiLoggedIn()}").document(friendid)
                .update("person", "you")
        }

    }
}