package com.petra.chatapp.notifications.network


import com.petra.chatapp.notifications.entity.PushNotification
import com.petra.chatapp.notifications.network.Constants.Companion.CONTENT_TYPE
import com.petra.chatapp.notifications.network.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: $SERVER_KEY", "Content-Type: $CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}