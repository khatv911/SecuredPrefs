package com.kay.receiver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.kay.prefprovider.PrefResolver

class MainActivity : AppCompatActivity() {

    private val prefResolver: PrefResolver by lazy {
        PrefResolver(
            "secured_token",
            contentResolver,
            "com.kay.securetoken.prefprovider"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textview = findViewById<TextView>(R.id.checkValue)

        textview.setText(prefResolver.getString("key1"))
    }
}
