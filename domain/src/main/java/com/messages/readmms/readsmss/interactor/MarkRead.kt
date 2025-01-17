
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.manager.NotificationManager
import com.messages.readmms.readsmss.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkRead @Inject constructor(
    private val messageRepo: MessageRepository,
    private val notificationManager: NotificationManager,
    private val updateBadge: UpdateBadge
) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadIds -> messageRepo.markRead(*threadIds) }
                .doOnNext { threadIds -> threadIds.forEach(notificationManager::update) }
                .flatMap { updateBadge.buildObservable(Unit) } // Update the badge
    }

}