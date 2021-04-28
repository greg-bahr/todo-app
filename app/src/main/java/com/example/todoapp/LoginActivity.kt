package com.example.todoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val response = IdpResponse.fromResultIntent(result.data)

                if (result.resultCode == Activity.RESULT_OK) {
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                } else {
                    Log.e("LoginActivity", "Login error: " + response?.error.toString())
                    finish()
                }
            }
        resultLauncher.launch(intent)
    }
}