package com.example.pizzastore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pizzaTypeSpinner: Spinner = findViewById(R.id.pizzaTypeSpinner)
        val pizzaSizeSpinner: Spinner = findViewById(R.id.pizzaSizeSpinner)
        val orderButton: Button = findViewById(R.id.orderButton)

        orderButton.setOnClickListener {
            val pizzaType = pizzaTypeSpinner.selectedItem.toString()
            val pizzaSize = pizzaSizeSpinner.selectedItem.toString()

            val intent = Intent(this, OrderConfirmationActivity::class.java)
            intent.putExtra("PIZZA_TYPE", pizzaType)
            intent.putExtra("PIZZA_SIZE", pizzaSize)
            startActivity(intent)
        }
    }
}
