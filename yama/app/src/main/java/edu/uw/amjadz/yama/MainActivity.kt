package edu.uw.amjadz.yama

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_main.*


private val MY_PERMISSIONS_REQUEST_READ_SMS = 3


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        checkForSmsReadPermission()

        fab.setOnClickListener { view ->
            val composeMessage = Intent(this, SendMessage::class.java)
            startActivity(composeMessage)


        }

        val listView = text_Messages


        if (fetchInbox() != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fetchInbox())
            listView.setAdapter(adapter)
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

    private fun checkForSmsReadPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){


        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_SMS),
                MY_PERMISSIONS_REQUEST_READ_SMS)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        if (item.itemId == R.id.action_settings) {
            val intent: Intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true

        } else {
            return super.onOptionsItemSelected(item)
        }
    }

}
