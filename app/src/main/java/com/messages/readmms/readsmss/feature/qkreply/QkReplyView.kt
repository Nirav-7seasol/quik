
package com.messages.readmms.readsmss.feature.qkreply

import com.messages.readmms.readsmss.common.base.QkView
import io.reactivex.Observable

interface QkReplyView : QkView<QkReplyState> {

    val menuItemIntent: Observable<Int>
    val textChangedIntent: Observable<CharSequence>
    val changeSimIntent: Observable<*>
    val sendIntent: Observable<Unit>

    fun setDraft(draft: String)
    fun finish()

}