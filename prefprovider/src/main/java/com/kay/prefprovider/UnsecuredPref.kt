package com.kay.prefprovider

class UnsecuredPref : PrefDelegate {

    override fun getString(key: String, defValue: String?): String? {
        return ""
    }

    override fun setString(key: String, value: String?) {
    }
}