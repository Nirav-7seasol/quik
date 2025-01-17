
package com.messages.readmms.readsmss.feature.themepicker.injection

import com.messages.readmms.readsmss.feature.themepicker.ThemePickerController
import com.messages.readmms.readsmss.injection.scope.ControllerScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ThemePickerModule(private val controller: ThemePickerController) {

    @Provides
    @ControllerScope
    @Named("recipientId")
    fun provideThreadId(): Long = controller.recipientId

}