package com.appweek04

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGreet = findViewById<Button>(R.id.buttonGreet)
        val buttonColor = findViewById<Button>(R.id.buttonColor)
        val buttonCount = findViewById<Button>(R.id.buttonCounter)

        buttonGreet.setOnClickListener {
            startActivity(Intent(this, GreetingActivity::class.java))
        }

        buttonColor.setOnClickListener {
            startActivity(Intent(this, ColorActivity::class.java))
        }

        buttonCount.setOnClickListener {
            startActivity(Intent(this, CounterActivity::class.java))
        }
    }
}