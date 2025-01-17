
package com.messages.readmms.readsmss.filter

abstract class Filter<in T> {

    abstract fun filter(item: T, query: CharSequence): Boolean

}