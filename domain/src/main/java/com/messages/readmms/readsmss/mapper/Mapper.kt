
package com.messages.readmms.readsmss.mapper

interface Mapper<in From, out To> {

    fun map(from: From): To

}
