package edu.uw.amjadz.yama

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.compose_message.*
import java.util.*
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent




private val MY_PERMISSIONS_REQUEST_SEND_SMS = 1
private val MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 2

class SendMessage : AppCompatActivity() {

    lateinit var smsSent: BroadcastReceiver
    lateinit var smsDelivered: BroadcastReceiver

    val SENT: String = "SMS_Sent"
    val DELIVERED: String = "SMS_DELIVERED"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_message)

        val filter = IntentFilter()
        filter.addAction("SMS_RECEIVED_ACTION")


        checkForSmsPermission()
        checkForSmsRecievePermission()

        sendSMS()

    }

    private fun sendSMS() {
        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), 0)
        val delivered = PendingIntent.getBroadcast(this,0, Intent(DELIVERED),0)

        checkForSmsPermission()
        checkForSmsRecievePermission()

        submit.setOnClickListener{

            var phoneNumber = phone_number.text.toString()
            var message = message_compose.text.toString()

            val sms = SmsManager.getDefault()
            sms.sendTextMessage(phoneNumber, null, message, sentPI,delivered)

            val listView = user_messages

            if (fetchInbox() != null) {
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fetchInbox())
                listView.setAdapter(adapter)
            }

        }
    }

    private fun fetchInbox(): ArrayList<String> {
        val sms = ArrayList<String>()

        val uriSms = Uri.parse("content://sms/inbox")
        val cursor =
            contentResolver.query(uriSms, arrayOf("_id", "address", "date", "body", "" + "person"), null, null, null)

        cursor!!.moveToFirst()

        while (cursor.moveToNext()) {
            val match = "is created"
            val address = cursor.getString(1)
            val body = cursor.getString(3)
            val tdata = body
            sms.add("" + "" + "From: " + address + "\n" + " Message: " + body)

        }

        return sms
    }

    override fun onResume() {
        super.onResume()

        smsSent = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when(resultCode){

                    Activity.RESULT_OK ->
                        Toast.makeText(context, "SMS sent!", Toast.LENGTH_SHORT).show()

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                        Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()

                    SmsManager.RESULT_ERROR_NO_SERVICE ->
                        Toast.makeText(context, "No Service!", Toast.LENGTH_SHORT).show()

                }
            }
        }

        smsDelivered = object: BroadcastReceiver(){
            override fun onReceive(context: Context, intent: Intent) {
                when(resultCode){

                    Activity.RESULT_OK ->
                        Toast.makeText(context, "SMS Delivered!", Toast.LENGTH_SHORT).show()

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                        Toast.makeText(context, "SMS Not Delivered!", Toast.LENGTH_SHORT).show()

                }

            }

        }

        registerReceiver(smsSent, IntentFilter(SENT))
        registerReceiver(smsDelivered, IntentFilter(DELIVERED))

    }

    override  fun onPause() {
        super.onPause()

        unregisterReceiver(smsSent)
        unregisterReceiver(smsDelivered)
    }

    private fun checkForSmsPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_SEND_SMS
                )
            }
        }
    }

    private fun checkForSmsRecievePermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){


        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                MY_PERMISSIONS_REQUEST_RECEIVE_SMS)
        }
    }

}