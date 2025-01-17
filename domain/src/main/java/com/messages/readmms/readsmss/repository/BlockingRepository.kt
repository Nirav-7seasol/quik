
package com.messages.readmms.readsmss.repository

import com.messages.readmms.readsmss.model.BlockedNumber
import io.realm.RealmResults

interface BlockingRepository {

    fun blockNumber(vararg addresses: String)

    fun getBlockedNumbers(): RealmResults<BlockedNumber>

    fun getBlockedNumber(id: Long): BlockedNumber?

    fun isBlocked(address: String): Boolean

    fun unblockNumber(id: Long)

    fun unblockNumbers(vararg addresses: String)

}
