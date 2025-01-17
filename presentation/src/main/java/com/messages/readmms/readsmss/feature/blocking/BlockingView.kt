
package com.messages.readmms.readsmss.feature.blocking

import com.messages.readmms.readsmss.common.base.QkViewContract
import io.reactivex.Observable

interface BlockingView : QkViewContract<BlockingState> {

    val blockingManagerIntent: Observable<*>
    val blockedNumbersIntent: Observable<*>
    val blockedMessagesIntent: Observable<*>
    val dropClickedIntent: Observable<*>

    fun openBlockingManager()
    fun openBlockedNumbers()
    fun openBlockedMessages()
}
