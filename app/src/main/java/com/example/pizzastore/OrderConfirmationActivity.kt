package com.example.pizzastore

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class OrderConfirmationActivity : AppCompatActivity() {

    private val CHANNEL_ID = "pizza_order_channel"
    private val NOTIFICATION_ID = 1
    private val READY_NOTIFICATION_ID = 2

    private lateinit var timerTextView: TextView
    private lateinit var handler: Handler
    private var timeLeftInMillis: Long = 30 * 60 * 1000 // 30 minutes in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        timerTextView = findViewById(R.id.timerTextView)
        val orderConfirmationTextView: TextView = findViewById(R.id.orderConfirmationTextView)

        val pizzaType = intent.getStringExtra("PIZZA_TYPE")
        val pizzaSize = intent.getStringExtra("PIZZA_SIZE")

        orderConfirmationTextView.text = "You ordered a $pizzaSize $pizzaType pizza!"

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                showNotification(pizzaType, pizzaSize)
                startTimer()
            } else {
                requestNotificationPermission()
            }
        } else {
            showNotification(pizzaType, pizzaSize)
            startTimer()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pizza Order Channel"
            val descriptionText = "Notification channel for pizza orders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000) // Vibration pattern
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null) // Default notification sound
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startTimer() {
        handler = Handler(Looper.getMainLooper())
        updateCountDownText()
        handler.postDelayed(timerRunnable, 1000)
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (timeLeftInMillis > 0) {
                timeLeftInMillis -= 1000
                updateCountDownText()
                handler.postDelayed(this, 1000)
            } else {
                showPizzaReadyNotification()
            }
        }
    }

    private fun showNotification(pizzaType: String?, pizzaSize: String?) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pizza)
            .setContentTitle("Pizza Order Confirmation")
            .setContentText("Your $pizzaSize $pizzaType pizza is being prepared!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 1000, 500, 1000)) // Vibration pattern
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Default notification sound
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, notification)
        }
    }

    private fun showPizzaReadyNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pizza)
            .setContentTitle("Pizza Ready")
            .setContentText("Your pizza is ready for pickup!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 1000, 500, 1000)) // Vibration pattern
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Default notification sound
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(READY_NOTIFICATION_ID, notification)
        }
    }

    private fun requestNotificationPermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val pizzaType = intent.getStringExtra("PIZZA_TYPE")
                val pizzaSize = intent.getStringExtra("PIZZA_SIZE")
                showNotification(pizzaType, pizzaSize)
                startTimer()
            } else {
                // Permission denied, handle accordingly
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
