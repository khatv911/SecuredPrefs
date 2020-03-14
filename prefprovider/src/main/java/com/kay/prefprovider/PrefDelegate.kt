package com.kay.prefprovider

import android.content.Context

/**
 * Implementation should wrap a actually SharedPreference inside
 */
internal interface PrefDelegate {
    fun getString(key: String, defValue: String? = null): String?
    fun setString(key: String, value: String? = null)
    //TODO: add more types to support

    companion object {
        fun create(context: Context, fileName: String): PrefDelegate {
            return if (fileName.startsWith("secured"))
                SecuredPref(context, fileName)
            else UnsecuredPref()
        }
    }
}