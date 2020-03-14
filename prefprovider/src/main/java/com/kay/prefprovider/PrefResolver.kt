package com.kay.prefprovider

import android.content.ContentResolver
import androidx.annotation.Nullable
import com.kay.prefprovider.PrefProvider.Companion.CODE_STRING
import com.kay.prefprovider.PrefProvider.Companion.createContentValues
import com.kay.prefprovider.PrefProvider.Companion.createQueryUri
import com.kay.prefprovider.PrefProvider.Companion.extractStringFromCursor
import com.kay.prefprovider.PrefProvider.Companion.performQuery


class PrefResolver constructor(
    private val fileName: String
    , private val resolver: ContentResolver,
    private val authority: String
) {
    fun setString(key: String, value: String) {
        resolver.update(createQueryUri(fileName, key, CODE_STRING, authority), createContentValues(key, value), null, null)
    }

    @Nullable
    fun getString(key: String, defaultValue: String? = null): String? {
        return extractStringFromCursor(
            performQuery(createQueryUri(fileName, key, CODE_STRING, authority), resolver),
            defaultValue
        )
    }
}