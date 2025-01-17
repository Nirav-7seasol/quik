
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.extensions.mapNotNull
import com.messages.readmms.readsmss.model.Message
import com.messages.readmms.readsmss.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class RetrySending @Inject constructor(private val messageRepo: MessageRepository) : Interactor<Long>() {

    override fun buildObservable(params: Long): Flowable<Message> {
        return Flowable.just(params)
                .doOnNext(messageRepo::markSending)
                .mapNotNull(messageRepo::getMessage)
                .doOnNext { message ->
                    when (message.isSms()) {
                        true -> messageRepo.sendSms(message)
                        false -> messageRepo.resendMms(message)
                    }
                }
    }

}
