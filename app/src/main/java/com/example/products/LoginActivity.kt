package com.example.products

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.products.databinding.ActivityLoginBinding
import com.example.products.databinding.ActivityShoppingBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if username and password are already saved in SharedPreferences
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            // If username or password is not present, set default values
            saveCredentials("gokul", "gokul_231")
        }
        binding.loginloginButton.setOnClickListener {
            // Get username and password from EditText fields
            val enteredUsername = binding.edEmailLogin.text.toString()
            val enteredPassword = binding.edPasswordLogin.text.toString()

            // If username or password is incorrect, show a Snackbar
            if (enteredUsername != username || enteredPassword != password) {
               Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.edEmailLogin.setText("")
            binding.edPasswordLogin.setText("")
            val intent = Intent(this, shopping::class.java)
            startActivity(intent)
            // Show a Snackbar to indicate successful login
            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveCredentials(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }
}