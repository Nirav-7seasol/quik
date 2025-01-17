
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.repository.BackupRepository
import io.reactivex.Flowable
import javax.inject.Inject

class PerformBackup @Inject constructor(
    private val backupRepo: BackupRepository
) : Interactor<Unit>() {

    override fun buildObservable(params: Unit): Flowable<*> {
        return Flowable.just(params)
                .doOnNext { backupRepo.performBackup() }
    }

}