
package com.messages.readmms.readsmss.model

import io.realm.RealmObject

open class SyncLog : RealmObject() {

    var date: Long = System.currentTimeMillis()

}