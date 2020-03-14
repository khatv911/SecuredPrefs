package com.kay.prefprovider

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecuredPref(context: Context, fileName: String) : PrefDelegate {

    private var sharedPreferences: SharedPreferences

    init {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            fileName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun getString(key: String, defValue: String?): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun setString(key: String, value: String?) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }
}