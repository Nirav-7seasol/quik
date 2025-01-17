
package com.messages.readmms.readsmss.feature.settings.swipe

import com.messages.readmms.readsmss.common.base.QkViewContract
import io.reactivex.Observable

interface SwipeActionsView : QkViewContract<SwipeActionsState> {

    enum class Action { LEFT, RIGHT }

    fun actionClicks(): Observable<Action>
    fun actionSelected(): Observable<Int>

    fun showSwipeActions(selected: Int)

}