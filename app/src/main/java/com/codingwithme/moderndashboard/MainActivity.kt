package com.codingwithme.moderndashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var clockctrl: LinearLayout = findViewById(R.id.btnClock)

        clockctrl.setOnClickListener{
            val intent = Intent(this, ClockControlActivity::class.java)
            startActivity(intent)
//            Toast.makeText(this@MainActivity, "Hallo Hallo Bandung", Toast.LENGTH_SHORT).show()
        }

        btnhistory.setOnClickListener{
            Toast.makeText(this@MainActivity, "COMING SOON", Toast.LENGTH_SHORT).show()
        }
    }
}