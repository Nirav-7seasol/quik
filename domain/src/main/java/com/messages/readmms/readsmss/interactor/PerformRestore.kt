
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.repository.BackupRepository
import io.reactivex.Flowable
import javax.inject.Inject

class PerformRestore @Inject constructor(
    private val backupRepo: BackupRepository
) : Interactor<String>() {

    override fun buildObservable(params: String): Flowable<*> {
        return Flowable.just(params)
            .doOnNext(backupRepo::performRestore)
    }

}
