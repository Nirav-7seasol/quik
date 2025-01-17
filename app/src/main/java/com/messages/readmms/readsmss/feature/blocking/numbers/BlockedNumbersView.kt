
package com.messages.readmms.readsmss.feature.blocking.numbers

import com.messages.readmms.readsmss.common.base.QkViewContract
import io.reactivex.Observable

interface BlockedNumbersView : QkViewContract<BlockedNumbersState> {

    fun unblockAddress(): Observable<Long>
    fun addAddress(): Observable<*>
    fun saveAddress(): Observable<String>

    fun showAddDialog()

}
