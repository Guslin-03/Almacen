package com.example.almacen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //espera y muestra login
        handler.postDelayed({ logIn() }, 3000)
    }

    private fun logIn() {
        val intent = Intent(this, AlmacenActivity::class.java)
        startActivity(intent)
        finish()
    }
}