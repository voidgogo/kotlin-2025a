package com.appweek03

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ColorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        val layoutMain = findViewById<LinearLayout>(R.id.layoutMain)
        val textViewCurrentColor = findViewById<TextView>(R.id.textViewCurrentColor)
        val buttonRed = findViewById<Button>(R.id.buttonRed)
        val buttonGreen = findViewById<Button>(R.id.buttonGreen)
        val buttonBlue = findViewById<Button>(R.id.buttonBlue)
        val buttonReset = findViewById<Button>(R.id.buttonReset)

        buttonRed.setOnClickListener {
            layoutMain.setBackgroundColor(Color.RED)
            textViewCurrentColor.text = "현재 색: 빨간색"
        }

        buttonGreen.setOnClickListener {
            layoutMain.setBackgroundColor(Color.GREEN)
            textViewCurrentColor.text = "현재 색: 초록색"
        }

        buttonBlue.setOnClickListener {
            layoutMain.setBackgroundColor(Color.BLUE)
            textViewCurrentColor.text = "현재 색: 파란색"
        }

        buttonReset.setOnClickListener {
            layoutMain.setBackgroundColor(Color.WHITE)
            textViewCurrentColor.text = "현재 색: 흰색"
        }
    }
}
