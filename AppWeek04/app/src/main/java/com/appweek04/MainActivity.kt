package com.appweek04

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val buttonColor = findViewById<Button>(R.id.buttonGreet)
        val buttonCounter = findViewById<Button>(R.id.buttonCounter)

//        buttonColor.setOnClickListener {
//            startActivity(Intent(this, GreetingActivity::class.java))
//        }
        buttonCounter.setOnClickListener {
            startActivity(Intent(this, CounterActivity::class.java))
        }
    }
}