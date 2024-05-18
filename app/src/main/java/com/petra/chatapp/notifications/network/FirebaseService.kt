@file:Suppress("DEPRECATION")

package com.petra.chatapp.notifications.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.petra.chatapp.MainActivity
import com.petra.chatapp.R
import com.petra.chatapp.SharedPrefs
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"
class FirebaseService: FirebaseMessagingService() {
    companion object{
        private const val KEY_REPLAY_TEXT = "KEY_REPLAY_TEXT"
        var sharedPrefs: SharedPreferences? = null
        var token:String? get()  {
            return sharedPrefs?.getString("token", "")
        }
            set(value) {
                sharedPrefs?.edit()?.putString("token", value)?.apply()
            }

    }
    override fun onNewToken(newtoken: String) {
        super.onNewToken(newtoken)
        token = newtoken
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            createNotifictionChannel(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val remoteInput = RemoteInput.Builder(KEY_REPLAY_TEXT).setLabel("Replay").build()
        val replayIntent = Intent(this, NotificationReply::class.java)
        val replayPendingIntent = PendingIntent.getBroadcast(this, 0, replayIntent, PendingIntent.FLAG_IMMUTABLE)
        val replayAction = NotificationCompat.Action.Builder(R.drawable.reply, "Replay", replayPendingIntent).addRemoteInput(remoteInput).build()
        val sharedCustomPref = SharedPrefs(applicationContext)
        sharedCustomPref.setIntValue("values", notificationID)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(Html.fromHtml("<b>${message.data["title"]}</b>: ${message.data["message"]}"))
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replayAction)
            .build()

        notificationManager.notify(notificationID, notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifictionChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My Channel Description"
            enableLights(true)
            lightColor = Color.GREEN

        }
        notificationManager.createNotificationChannel(channel)

    }

}