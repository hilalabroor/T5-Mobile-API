package com.example.loginapiapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.loginapiapp.model.LoginRequest
import com.example.loginapiapp.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Email dan password wajib diisi")
            return
        }

        lifecycleScope.launch {
            showLoading(true)

            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val loginData = response.body()?.data
                    val userName = loginData?.user?.name.orEmpty()
                    val token = loginData?.token.orEmpty()

                    if (userName.isNotEmpty()) {
                        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                        prefs.edit().putString("token", token).apply()

                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        intent.putExtra(HomeActivity.EXTRA_NAME, userName)
                        startActivity(intent)
                    } else {
                        showMessage("Data user tidak ditemukan")
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Email atau password salah"
                    showMessage(errorMessage)
                }
            } catch (e: Exception) {
                showMessage("Tidak dapat terhubung ke server: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}