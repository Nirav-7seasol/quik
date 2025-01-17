
package com.messages.readmms.readsmss.feature.themepicker.injection

import com.messages.readmms.readsmss.feature.themepicker.ThemePickerController
import com.messages.readmms.readsmss.injection.scope.ControllerScope
import dagger.Subcomponent

@ControllerScope
@Subcomponent(modules = [ThemePickerModule::class])
interface ThemePickerComponent {

    fun inject(controller: ThemePickerController)

    @Subcomponent.Builder
    interface Builder {
        fun themePickerModule(module: ThemePickerModule): Builder
        fun build(): ThemePickerComponent
    }

}