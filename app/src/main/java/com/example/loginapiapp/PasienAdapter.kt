package com.example.loginapiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapiapp.model.Pasien

class PasienAdapter(private val listPasien: List<Pasien>) :
    RecyclerView.Adapter<PasienAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaPasien)
        val tvDetail: TextView = view.findViewById(R.id.tvDetailPasien)
        val tvAlamat: TextView = view.findViewById(R.id.tvAlamat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pasien, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pasien = listPasien[position]

        holder.tvNama.text = pasien.nama

        val jenisKelamin = if (pasien.jenis_kelamin == "L") "Laki-laki" else "Perempuan"

        holder.tvDetail.text = "$jenisKelamin | ${pasien.tanggal_lahir} | ${pasien.no_telepon}"
        holder.tvAlamat.text = pasien.alamat
    }

    override fun getItemCount(): Int = listPasien.size
}