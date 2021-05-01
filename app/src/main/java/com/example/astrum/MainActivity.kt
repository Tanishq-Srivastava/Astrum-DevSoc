package com.example.astrum

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    // Request code for sign in
    val RC_SIGN_IN = 123
    lateinit var auth: FirebaseAuth
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    lateinit var mUsername: String

    // creating a variable for
    // our Firebase Database.
    lateinit var firebaseDatabase: FirebaseDatabase

    // creating a variable for our
    // Database Reference for Firebase.
    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS,Manifest.permission.RECORD_AUDIO),
            PackageManager.PERMISSION_GRANTED
        )

        auth = FirebaseAuth.getInstance()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            sendMessage()
            escape()
        }

        // below line is used to get the instance of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.reference.child("Silent_Mode")

        // calling method for getting data.
        getdata()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_sos, R.id.nav_contactus
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Login Flow
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                // Already signed in
                onSignedInInitialize(user.displayName)
                //Toast.makeText(this, "You're now signed in!", Toast.LENGTH_LONG).show()
                //startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                // Not signed in. Start the login flow.
                onSignedOutCleanup()
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                            listOf(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build()
                            )
                        )
                        .setTheme(R.style.AuthenticationTheme)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                    RC_SIGN_IN
                )
            }
        }
    }

    //sending SMS using SMSManager API
    fun sendMessage() {
        var smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            "8956459506",
            null,
            "I am in danger! Reach out at the coordinates Latitude: 15.47914, Longitude: 73.8209788",
            null,
            null
        )
        Toast.makeText(this, "SOS Sent Successfully!", Toast.LENGTH_SHORT).show()
    }

    //Google Maps turn by turn navigation
    fun escape()
    {
       val googleMapsUrl = "google.navigation:q=15.49010396751634, 73.82004252609961&mode=w"
        val uri = Uri.parse(googleMapsUrl)

        val googleMapsPackage = "com.google.android.apps.maps"
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage(googleMapsPackage) }
        startActivity(intent)
    }

    private fun getdata() {

        // calling add value event listener method
        // for getting the values from database.
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.
                val value = snapshot.getValue(String::class.java)
                if (value.equals("begin")) {

                    // after getting the value we are setting
                    // our value to our text view in below line.
                    Log.i("Value Read", "Value Read " + value)
                    //WRITE SAME FUNCTION AS OF BUTTON
                    sendMessage()
                    escape()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(this@MainActivity, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    //Login Flow
    override fun onPause() {
        super.onPause()
        auth.removeAuthStateListener(authStateListener)
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(authStateListener)
    }

    private fun onSignedInInitialize(username: String?) {
        if (username != null) mUsername = username
    }

    private fun onSignedOutCleanup() {
        mUsername = ""
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // RC_SIGN_IN is the request code you passed when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                Toast.makeText(this, "Signed in", Toast.LENGTH_LONG).show()
                //startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                return
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login", "Login canceled by User")
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Log.e("Login", "No Internet Connection")
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login", "Unknown Error")
                    return
                }
            }
            Log.e("Login", "Unknown sign in response")
        }
    }
}
