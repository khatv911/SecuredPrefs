package com.kay.securetoken

import android.content.ContentResolver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        saveToken()
    }

    private fun saveToken() {
        prefResolver.setString("key1", "myString")
    }

}
