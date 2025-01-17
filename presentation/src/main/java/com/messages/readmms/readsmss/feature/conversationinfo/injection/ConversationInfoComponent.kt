
package com.messages.readmms.readsmss.feature.conversationinfo.injection

import com.messages.readmms.readsmss.feature.conversationinfo.ConversationInfoController
import com.messages.readmms.readsmss.injection.scope.ControllerScope
import dagger.Subcomponent

@ControllerScope
@Subcomponent(modules = [ConversationInfoModule::class])
interface ConversationInfoComponent {

    fun inject(controller: ConversationInfoController)

    @Subcomponent.Builder
    interface Builder {
        fun conversationInfoModule(module: ConversationInfoModule): Builder
        fun build(): ConversationInfoComponent
    }

}