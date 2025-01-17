
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class SaveImage @Inject constructor(
    private val messageRepo: MessageRepository
) : Interactor<Long>() {

    override fun buildObservable(params: Long): Flowable<*> {
        return Flowable.just(params)
                .doOnNext { partId -> messageRepo.savePart(partId) }
    }

}