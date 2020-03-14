package com.kay.prefprovider

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import androidx.annotation.Nullable


class PrefProvider : ContentProvider() {

    /**
     * The authority to be loaded from resource
     *
     */
    private var authority: String? = null

    /**
     * UriMatcher
     */
    private lateinit var uriMatcher: UriMatcher

    /**
     * use shared_pref file name as key for the map
     */
    private val prefDelegates = mutableMapOf<String, PrefDelegate>()

    override fun onCreate(): Boolean {
        authority = context?.getString(R.string.pref_provider_authority)?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            ?: throw  IllegalArgumentException("Empty or blank authority, pls set a proper value for <pref_provider_authority>")
        uriMatcher = createUriMatcher(authority!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        checkNotNull(authority)
        return try {
            val prefDelegate = getDelegate(uri.pathSegments[1])
            val key = uri.pathSegments[2]
            when (uriMatcher.match(uri)) {
                CODE_STRING -> preferenceToCursor(prefDelegate.getString(key))
                else -> null
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("PrefProvider", " indexOutBound ${e.message}")
            null
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        checkNotNull(authority)
        checkNotNull(values, { "What were you thinking?!?" })

        try {
            val prefDelegate = getDelegate(uri.pathSegments[1])
            val key = uri.pathSegments[2]
            when (uriMatcher.match(uri)) {
                CODE_STRING -> {
                    val s = values.getAsString(VALUE)
                    prefDelegate.setString(key, s)
                }
                else -> {
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("PrefProvider", " indexOutBound ${e.message}")

        }
        // no need to count changed rows, cause there is no row
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        checkNotNull(authority)
        try {
            val prefDelegate = getDelegate(uri.pathSegments[1])
            val key = uri.pathSegments[2]
            when (uriMatcher.match(uri)) {
                CODE_STRING -> {
                    prefDelegate.setString(key, null)
                }
                else -> {
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("PrefProvider", " indexOutBound ${e.message}")

        }
        return 0
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("getType is not supported!")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Use update function instead!")
    }


    /**
     * Convert a value into a cursor object using a Matrix Cursor
     *
     * @param value the value to be converetd
     * @param <T>   generic object type
     * @return a Cursor object
    </T> */
    private fun <T> preferenceToCursor(value: T?): MatrixCursor? {
        if (value == null) return null
        val matrixCursor = MatrixCursor(arrayOf(VALUE), 1)
        val builder = matrixCursor.newRow()
        builder.add(value)
        return matrixCursor
    }

    private fun getDelegate(fileName: String): PrefDelegate =
        prefDelegates[fileName] ?: PrefDelegate.create(
            context ?: throw IllegalStateException("Null context"),
            fileName
        ).also { prefDelegates[fileName] = it }

    companion object {
        private const val KEY = "key"
        const val VALUE = "value"
        const val CODE_STRING = 1

        private fun createUriMatcher(authority: String) = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(authority, "string/*/*", CODE_STRING)
        }

        @JvmStatic
        fun extractStringFromCursor(cursor: Cursor?, defaultVal: String?): String? {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(VALUE));
                }
                cursor.close();
            }
            return defaultVal
        }

        @JvmStatic
        fun <T> createContentValues(key: String, value: T): ContentValues {
            val contentValues = ContentValues()
            contentValues.put(KEY, key)
            if (value is String) {
                contentValues.put(VALUE, value as String)
            } else {
                throw IllegalArgumentException("Unsupported type ")
            }
            return contentValues
        }

        @JvmStatic
        @Nullable
        fun performQuery(uri: Uri, resolver: ContentResolver): Cursor? {
            return resolver.query(uri, null, null, null, null, null)
        }

        @JvmStatic
        fun createQueryUri(fileName: String, key: String, prefType: Int, authority: String): Uri {
            return when (prefType) {
                CODE_STRING -> Uri.parse("content://$authority/string/$fileName/$key")
                else -> throw UnsupportedOperationException()
            }
        }

    }
}