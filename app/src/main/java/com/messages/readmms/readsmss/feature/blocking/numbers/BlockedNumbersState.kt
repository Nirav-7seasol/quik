
package com.messages.readmms.readsmss.feature.blocking.numbers

import com.messages.readmms.readsmss.model.BlockedNumber
import io.realm.RealmList
import io.realm.RealmResults

data class BlockedNumbersState(
    val numbers: RealmResults<BlockedNumber>? = null
)
