package com.messages.readmms.readsmss.common

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.fromJson(json: String?): T =
    fromJson<T>(json, object : TypeToken<T>() {}.type)


fun Context.getDrawable(drawableName: String): Drawable? {
    return try {
        val resources: Resources = resources
        val resourceId = resources.getIdentifier(drawableName, "drawable", packageName)
        resources.getDrawable(resourceId)
    } catch (e: Exception) {
        null
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}