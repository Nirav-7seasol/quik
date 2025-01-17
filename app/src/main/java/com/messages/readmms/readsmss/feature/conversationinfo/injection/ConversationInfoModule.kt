
package com.messages.readmms.readsmss.feature.conversationinfo.injection

import com.messages.readmms.readsmss.feature.conversationinfo.ConversationInfoController
import com.messages.readmms.readsmss.injection.scope.ControllerScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ConversationInfoModule(private val controller: ConversationInfoController) {

    @Provides
    @ControllerScope
    @Named("threadId")
    fun provideThreadId(): Long = controller.threadId

}