/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.messages.readmms.readsmss.injection

import com.messages.readmms.readsmss.common.App
import com.messages.readmms.readsmss.common.QkDialog
import com.messages.readmms.readsmss.common.util.QkChooserTargetService
import com.messages.readmms.readsmss.common.widget.AvatarView
import com.messages.readmms.readsmss.common.widget.PagerTitleView
import com.messages.readmms.readsmss.common.widget.PreferenceView
import com.messages.readmms.readsmss.common.widget.QkEditText
import com.messages.readmms.readsmss.common.widget.QkSwitch
import com.messages.readmms.readsmss.common.widget.QkTextView
import com.messages.readmms.readsmss.common.widget.RadioPreferenceView
import com.messages.readmms.readsmss.feature.backup.BackupController
import com.messages.readmms.readsmss.feature.blocking.BlockingController
import com.messages.readmms.readsmss.feature.blocking.manager.BlockingManagerController
import com.messages.readmms.readsmss.feature.blocking.messages.BlockedMessagesController
import com.messages.readmms.readsmss.feature.blocking.numbers.BlockedNumbersController
import com.messages.readmms.readsmss.feature.compose.editing.DetailedChipView
import com.messages.readmms.readsmss.feature.conversationinfo.injection.ConversationInfoComponent
import com.messages.readmms.readsmss.feature.settings.SettingsController
import com.messages.readmms.readsmss.feature.settings.about.AboutController
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

    fun inject(controller: AboutController)
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
    fun inject(view: QkEditText)
    fun inject(view: QkSwitch)
    fun inject(view: QkTextView)

}
