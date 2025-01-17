
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.manager.NotificationManager
import com.messages.readmms.readsmss.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkFailed @Inject constructor(
    private val messageRepo: MessageRepository,
    private val notificationManager: NotificationManager
) : Interactor<MarkFailed.Params>() {

    data class Params(val id: Long, val resultCode: Int)

    override fun buildObservable(params: Params): Flowable<Unit> {
        return Flowable.just(Unit)
                .doOnNext { messageRepo.markFailed(params.id, params.resultCode) }
                .doOnNext { notificationManager.notifyFailed(params.id) }
    }

}