
package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.manager.ShortcutManager
import com.messages.readmms.readsmss.manager.WidgetManager
import io.reactivex.Flowable
import javax.inject.Inject

class UpdateBadge @Inject constructor(
    private val shortcutManager: ShortcutManager,
    private val widgetManager: WidgetManager
) : Interactor<Unit>() {

    override fun buildObservable(params: Unit): Flowable<*> {
        return Flowable.just(params)
                .doOnNext { shortcutManager.updateBadge() }
                .doOnNext { widgetManager.updateUnreadCount() }
    }

}