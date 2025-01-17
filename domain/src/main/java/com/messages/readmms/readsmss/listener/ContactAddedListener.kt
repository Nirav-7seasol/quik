
package com.messages.readmms.readsmss.listener

import io.reactivex.Observable

interface ContactAddedListener {

    fun listen(): Observable<*>

}
