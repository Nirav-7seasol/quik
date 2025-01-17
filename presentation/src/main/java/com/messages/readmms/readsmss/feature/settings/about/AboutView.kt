
package com.messages.readmms.readsmss.feature.settings.about

import com.messages.readmms.readsmss.common.base.QkViewContract
import com.messages.readmms.readsmss.common.widget.PreferenceView
import io.reactivex.Observable

interface AboutView : QkViewContract<Unit> {

    fun preferenceClicks(): Observable<PreferenceView>

}