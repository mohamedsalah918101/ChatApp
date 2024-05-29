
package com.petra.chatapp.notifications.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.petra.chatapp.MainActivity
import com.petra.chatapp.R
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"
class FirebaseService: FirebaseMessagingService() {
    companion object{
        private const val KEY_REPLAY_TEXT = "KEY_REPLAY_TEXT"
        var token: String? = null

    }
    override fun onNewToken(newtoken: String) {
        super.onNewToken(newtoken)
        token = newtoken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCMService", "Message data payload: ${message.data}")

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotifictionChannel(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val remoteInput = RemoteInput.Builder(KEY_REPLAY_TEXT).setLabel("Replay").build()
        val replayIntent = Intent(this, NotificationReply::class.java)
        val replayPendingIntent = PendingIntent.getBroadcast(this, 0, replayIntent, PendingIntent.FLAG_IMMUTABLE)
        val replayAction = NotificationCompat.Action.Builder(R.drawable.reply, "Replay", replayPendingIntent).addRemoteInput(remoteInput).build()


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.chatapp)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
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