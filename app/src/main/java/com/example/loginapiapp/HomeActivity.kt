package com.example.loginapiapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapiapp.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
    }

    private lateinit var rvPasien: RecyclerView
    private lateinit var pbHome: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val tvName = findViewById<TextView>(R.id.tvName)
        tvName.text = name

        rvPasien = findViewById(R.id.rvPasien)
        pbHome = findViewById(R.id.pbHome)

        rvPasien.layoutManager = LinearLayoutManager(this)

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        fetchDataPasien()
    }

    private fun fetchDataPasien() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", "")

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        lifecycleScope.launch {
            pbHome.visibility = View.VISIBLE
            try {
                val response = RetrofitClient.apiService.getPasien("Bearer $token")

                if (response.isSuccessful) {
                    val listPasien = response.body()?.data ?: emptyList()

                    rvPasien.adapter = PasienAdapter(listPasien)
                } else {
                    Toast.makeText(this@HomeActivity, "Gagal memuat data pasien", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                pbHome.visibility = View.GONE
            }
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Yakin ingin logout?")
            .setPositiveButton("Logout") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit().remove("token").apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}