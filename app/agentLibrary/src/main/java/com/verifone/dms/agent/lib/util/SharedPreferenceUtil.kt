package com.verifone.dms.agent.lib.util

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceUtil {
    private const val PREFERENCES = "DMS"

    fun setStringPreferences(
        context: Context, key: String?,
        value: String?
    ) {
        val setting: SharedPreferences = context
            .getSharedPreferences(PREFERENCES, 0)
        val editor = setting.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringPreferences(context: Context, key: String?): String? {
        val setting: SharedPreferences = context.getSharedPreferences(PREFERENCES, 0)
        return setting.getString(key, "")
    }

    fun setIntegerPreferences(context: Context, key: String?, value: Int) {
        val setting: SharedPreferences = context.getSharedPreferences(PREFERENCES, 0)
        val editor = setting.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getIntegerPreferences(context: Context, key: String?): Int {
        val setting: SharedPreferences = context
            .getSharedPreferences(PREFERENCES, 0)
        return setting.getInt(key, 0)
    }

    fun clearAllSharedPreferences(context: Context) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }

    fun clearSharedPreferencesByKey(context: Context, key: String?) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.remove(key)
        editor.apply()
    }

    fun setBooleanPreferences(
        context: Context, key: String?,
        isCheck: Boolean
    ) {
        val setting = context
            .getSharedPreferences(PREFERENCES, 0)
        val editor = setting.edit()
        editor.putBoolean(key, isCheck)
        editor.apply()
    }

    fun getBooleanPreferences(context: Context, key: String?): Boolean {
        val setting = context
            .getSharedPreferences(PREFERENCES, 0)
        return setting.getBoolean(key, false)
    }


}