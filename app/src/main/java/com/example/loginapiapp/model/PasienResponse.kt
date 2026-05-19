package com.example.loginapiapp.model

data class PasienResponse(
    val success: Boolean,
    val message: String,
    val data: List<Pasien>
)

data class Pasien(
    val id: Int,
    val nama: String,
    val tanggal_lahir: String,
    val jenis_kelamin: String,
    val alamat: String,
    val no_telepon: String,
    val created_at: String?,
    val updated_at: String?
)