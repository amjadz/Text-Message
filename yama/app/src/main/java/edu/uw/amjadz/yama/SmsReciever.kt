package edu.uw.amjadz.yama

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.ArrayAdapter
import android.widget.Toast
import java.util.*

class SmsReciever: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if(extras != null) {
            val sms = extras.get("pdus") as Array<Any>

            for(i in sms.indices){
                val format = extras.getString("format")

                var smsMessage = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[i] as ByteArray, format)

                } else {
                    SmsMessage.createFromPdu(sms[i] as ByteArray)

                }

                val phoneNumber = smsMessage.originatingAddress
                val message = smsMessage.messageBody.toString()

                Toast.makeText(context, "phoneNumber: $phoneNumber" + "message: $message", Toast.LENGTH_SHORT). show()

                createWebNotification(context, phoneNumber, message)

                val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


                if(preference.getBoolean("auto_reply", false)){
                    val sms = SmsManager.getDefault()

                    sms.sendTextMessage(phoneNumber, null, "This is an automatic Message", null, null)


                }


            }

        }

    }

    fun createWebNotification(context: Context,title: String, text: String) {
        val channelId = context.getString(edu.uw.amjadz.yama.R.string.default_notification)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setSmallIcon(edu.uw.amjadz.yama.R.drawable.notification_icon_background)
        notificationBuilder.setContentTitle(title)
        notificationBuilder.setContentText(text)
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND)


        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // pending implicit intent to view url
        val intent: Intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pending)
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Aware CloudAlert",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
        }
        // using the same tag and Id causes the new notification to replace an existing one
        val random = Random()
        mNotificationManager.notify(
            System.currentTimeMillis().toString(),
            random.nextInt(),
            notificationBuilder.build()
        )
    }


}
