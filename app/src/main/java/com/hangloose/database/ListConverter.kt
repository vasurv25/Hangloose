package com.hangloose.database

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


public class ListConverter {

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(valueList: List<String>?): String? {
        val gson = Gson()
        return gson.toJson(valueList)
    }
}