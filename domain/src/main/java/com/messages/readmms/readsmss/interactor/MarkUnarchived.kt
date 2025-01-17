
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.repository.ConversationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkUnarchived @Inject constructor(private val conversationRepo: ConversationRepository) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadIds -> conversationRepo.markUnarchived(*threadIds) }
    }

}