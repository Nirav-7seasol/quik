
package com.messages.readmms.readsmss.feature.blocking

import android.content.Context
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.blocking.BlockingClient
import com.messages.readmms.readsmss.common.base.QkPresenter
import com.messages.readmms.readsmss.util.Preferences
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class BlockingPresenter @Inject constructor(
    context: Context,
    private val blockingClient: BlockingClient,
    private val prefs: Preferences
) : QkPresenter<BlockingView, BlockingState>(BlockingState()) {

    init {
        disposables += prefs.blockingManager.asObservable()
                .map { client ->
                    when (client) {
                        Preferences.BLOCKING_MANAGER_CB -> R.string.blocking_manager_call_blocker_title
                        Preferences.BLOCKING_MANAGER_CC -> R.string.blocking_manager_call_control_title
                        Preferences.BLOCKING_MANAGER_SIA -> R.string.blocking_manager_sia_title
                        else -> R.string.blocking_manager_sms_title
                    }
                }
                .map(context::getString)
                .subscribe { manager -> newState { copy(blockingManager = manager) } }

        disposables += prefs.drop.asObservable()
                .subscribe { enabled -> newState { copy(dropEnabled = enabled) } }
    }

    override fun bindIntents(view: BlockingView) {
        super.bindIntents(view)

        view.blockingManagerIntent
                .autoDisposable(view.scope())
                .subscribe { view.openBlockingManager() }

        view.blockedNumbersIntent
                .autoDisposable(view.scope())
                .subscribe {
                    if (prefs.blockingManager.get() == Preferences.BLOCKING_MANAGER_QKSMS) {
                        // TODO: This is a hack, get rid of it once we implement AndroidX navigation
                        view.openBlockedNumbers()
                    } else {
                        blockingClient.openSettings()
                    }
                }

        view.blockedMessagesIntent
                .autoDisposable(view.scope())
                .subscribe { view.openBlockedMessages() }

        view.dropClickedIntent
                .autoDisposable(view.scope())
                .subscribe { prefs.drop.set(!prefs.drop.get()) }
    }

}
