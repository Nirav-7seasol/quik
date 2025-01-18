
package com.messages.readmms.readsmss.injection

import com.messages.readmms.readsmss.common.App
import com.messages.readmms.readsmss.common.QkDialog
import com.messages.readmms.readsmss.common.util.QkChooserTargetService
import com.messages.readmms.readsmss.common.widget.AvatarView
import com.messages.readmms.readsmss.common.widget.PagerTitleView
import com.messages.readmms.readsmss.common.widget.PreferenceView
import com.messages.readmms.readsmss.common.widget.MyEditText
import com.messages.readmms.readsmss.common.widget.MySwitch
import com.messages.readmms.readsmss.common.widget.MyTextView
import com.messages.readmms.readsmss.common.widget.RadioPreferenceView
import com.messages.readmms.readsmss.feature.backup.BackupController
import com.messages.readmms.readsmss.feature.blocking.BlockingController
import com.messages.readmms.readsmss.feature.blocking.manager.BlockingManagerController
import com.messages.readmms.readsmss.feature.blocking.messages.BlockedMessagesController
import com.messages.readmms.readsmss.feature.blocking.numbers.BlockedNumbersController
import com.messages.readmms.readsmss.feature.compose.editing.DetailedChipView
import com.messages.readmms.readsmss.feature.conversationinfo.injection.ConversationInfoComponent
import com.messages.readmms.readsmss.feature.settings.SettingsController
import com.messages.readmms.readsmss.feature.settings.swipe.SwipeActionsController
import com.messages.readmms.readsmss.feature.themepicker.injection.ThemePickerComponent
import com.messages.readmms.readsmss.feature.widget.WidgetAdapter
import com.messages.readmms.readsmss.injection.android.ActivityBuilderModule
import com.messages.readmms.readsmss.injection.android.BroadcastReceiverBuilderModule
import com.messages.readmms.readsmss.injection.android.ServiceBuilderModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import com.messages.readmms.readsmss.common.widget.PreferenceViewWithBinding
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuilderModule::class,
    BroadcastReceiverBuilderModule::class,
    ServiceBuilderModule::class])
interface AppComponent {

    fun conversationInfoBuilder(): ConversationInfoComponent.Builder
    fun themePickerBuilder(): ThemePickerComponent.Builder

    fun inject(application: App)

    fun inject(controller: BackupController)
    fun inject(controller: BlockedMessagesController)
    fun inject(controller: BlockedNumbersController)
    fun inject(controller: BlockingController)
    fun inject(controller: BlockingManagerController)
    fun inject(controller: SettingsController)
    fun inject(controller: SwipeActionsController)

    fun inject(dialog: QkDialog)

    fun inject(service: WidgetAdapter)

    /**
     * This can't use AndroidInjection, or else it will crash on pre-marshmallow devices
     */
    fun inject(service: QkChooserTargetService)

    fun inject(view: AvatarView)
    fun inject(view: DetailedChipView)
    fun inject(view: PagerTitleView)
    fun inject(view: PreferenceView)
    fun inject(view: PreferenceViewWithBinding)
    fun inject(view: RadioPreferenceView)
    fun inject(view: MyEditText)
    fun inject(view: MySwitch)
    fun inject(view: MyTextView)

}
