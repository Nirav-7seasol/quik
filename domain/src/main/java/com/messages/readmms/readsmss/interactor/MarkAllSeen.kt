
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkAllSeen @Inject constructor(private val messageRepo: MessageRepository) : Interactor<Unit>() {

    override fun buildObservable(params: Unit): Flowable<Unit> {
        return Flowable.just(Unit)
                .doOnNext { messageRepo.markAllSeen() }
    }

}