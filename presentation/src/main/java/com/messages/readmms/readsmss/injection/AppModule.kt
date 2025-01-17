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

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.messages.readmms.readsmss.manager.ReferralManagerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import com.messages.readmms.readsmss.blocking.BlockingClient
import com.messages.readmms.readsmss.blocking.BlockingManager
import com.messages.readmms.readsmss.common.ViewModelFactory
import com.messages.readmms.readsmss.common.util.NotificationManagerImpl
import com.messages.readmms.readsmss.common.util.ShortcutManagerImpl
import com.messages.readmms.readsmss.feature.conversationinfo.injection.ConversationInfoComponent
import com.messages.readmms.readsmss.feature.themepicker.injection.ThemePickerComponent
import com.messages.readmms.readsmss.listener.ContactAddedListener
import com.messages.readmms.readsmss.listener.ContactAddedListenerImpl
import com.messages.readmms.readsmss.manager.ActiveConversationManager
import com.messages.readmms.readsmss.manager.ActiveConversationManagerImpl
import com.messages.readmms.readsmss.manager.AlarmManager
import com.messages.readmms.readsmss.manager.AlarmManagerImpl
import com.messages.readmms.readsmss.manager.ChangelogManager
import com.messages.readmms.readsmss.manager.ChangelogManagerImpl
import com.messages.readmms.readsmss.manager.KeyManager
import com.messages.readmms.readsmss.manager.KeyManagerImpl
import com.messages.readmms.readsmss.manager.NotificationManager
import com.messages.readmms.readsmss.manager.PermissionManager
import com.messages.readmms.readsmss.manager.PermissionManagerImpl
import com.messages.readmms.readsmss.manager.RatingManager
import com.messages.readmms.readsmss.manager.ReferralManager
import com.messages.readmms.readsmss.manager.ShortcutManager
import com.messages.readmms.readsmss.manager.WidgetManager
import com.messages.readmms.readsmss.manager.WidgetManagerImpl
import com.messages.readmms.readsmss.mapper.CursorToContact
import com.messages.readmms.readsmss.mapper.CursorToContactGroup
import com.messages.readmms.readsmss.mapper.CursorToContactGroupImpl
import com.messages.readmms.readsmss.mapper.CursorToContactGroupMember
import com.messages.readmms.readsmss.mapper.CursorToContactGroupMemberImpl
import com.messages.readmms.readsmss.mapper.CursorToContactImpl
import com.messages.readmms.readsmss.mapper.CursorToConversation
import com.messages.readmms.readsmss.mapper.CursorToConversationImpl
import com.messages.readmms.readsmss.mapper.CursorToMessage
import com.messages.readmms.readsmss.mapper.CursorToMessageImpl
import com.messages.readmms.readsmss.mapper.CursorToPart
import com.messages.readmms.readsmss.mapper.CursorToPartImpl
import com.messages.readmms.readsmss.mapper.CursorToRecipient
import com.messages.readmms.readsmss.mapper.CursorToRecipientImpl
import com.messages.readmms.readsmss.mapper.RatingManagerImpl
import com.messages.readmms.readsmss.repository.BackupRepository
import com.messages.readmms.readsmss.repository.BackupRepositoryImpl
import com.messages.readmms.readsmss.repository.BlockingRepository
import com.messages.readmms.readsmss.repository.BlockingRepositoryImpl
import com.messages.readmms.readsmss.repository.ContactRepository
import com.messages.readmms.readsmss.repository.ContactRepositoryImpl
import com.messages.readmms.readsmss.repository.ConversationRepository
import com.messages.readmms.readsmss.repository.ConversationRepositoryImpl
import com.messages.readmms.readsmss.repository.MessageRepository
import com.messages.readmms.readsmss.repository.MessageRepositoryImpl
import com.messages.readmms.readsmss.repository.ScheduledMessageRepository
import com.messages.readmms.readsmss.repository.ScheduledMessageRepositoryImpl
import com.messages.readmms.readsmss.repository.SyncRepository
import com.messages.readmms.readsmss.repository.SyncRepositoryImpl
import javax.inject.Singleton

@Module(
    subcomponents = [
        ConversationInfoComponent::class,
        ThemePickerComponent::class]
)
class AppModule(private var application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = application

    @Provides
    fun provideContentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRxPreferences(preferences: SharedPreferences): RxSharedPreferences {
        return RxSharedPreferences.create(preferences)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory = factory

    // Listener

    @Provides
    fun provideContactAddedListener(listener: ContactAddedListenerImpl): ContactAddedListener =
        listener

    // Manager

//    @Provides
//    fun provideBillingManager(manager: BillingManager): BillingManager = manager

    @Provides
    fun provideActiveConversationManager(manager: ActiveConversationManagerImpl): ActiveConversationManager =
        manager

    @Provides
    fun provideAlarmManager(manager: AlarmManagerImpl): AlarmManager = manager

    @Provides
    fun blockingClient(manager: BlockingManager): BlockingClient = manager

    @Provides
    fun changelogManager(manager: ChangelogManagerImpl): ChangelogManager = manager

    @Provides
    fun provideKeyManager(manager: KeyManagerImpl): KeyManager = manager

    @Provides
    fun provideNotificationsManager(manager: NotificationManagerImpl): NotificationManager = manager

    @Provides
    fun providePermissionsManager(manager: PermissionManagerImpl): PermissionManager = manager

    @Provides
    fun provideRatingManager(manager: RatingManagerImpl): RatingManager = manager

    @Provides
    fun provideShortcutManager(manager: ShortcutManagerImpl): ShortcutManager = manager

    @Provides
    fun provideReferralManager(manager: ReferralManagerImpl): ReferralManager = manager

    @Provides
    fun provideWidgetManager(manager: WidgetManagerImpl): WidgetManager = manager

    // Mapper

    @Provides
    fun provideCursorToContact(mapper: CursorToContactImpl): CursorToContact = mapper

    @Provides
    fun provideCursorToContactGroup(mapper: CursorToContactGroupImpl): CursorToContactGroup = mapper

    @Provides
    fun provideCursorToContactGroupMember(mapper: CursorToContactGroupMemberImpl): CursorToContactGroupMember =
        mapper

    @Provides
    fun provideCursorToConversation(mapper: CursorToConversationImpl): CursorToConversation = mapper

    @Provides
    fun provideCursorToMessage(mapper: CursorToMessageImpl): CursorToMessage = mapper

    @Provides
    fun provideCursorToPart(mapper: CursorToPartImpl): CursorToPart = mapper

    @Provides
    fun provideCursorToRecipient(mapper: CursorToRecipientImpl): CursorToRecipient = mapper

    // Repository

    @Provides
    fun provideBackupRepository(repository: BackupRepositoryImpl): BackupRepository = repository

    @Provides
    fun provideBlockingRepository(repository: BlockingRepositoryImpl): BlockingRepository =
        repository

    @Provides
    fun provideContactRepository(repository: ContactRepositoryImpl): ContactRepository = repository

    @Provides
    fun provideConversationRepository(repository: ConversationRepositoryImpl): ConversationRepository =
        repository

    @Provides
    fun provideMessageRepository(repository: MessageRepositoryImpl): MessageRepository = repository

    @Provides
    fun provideScheduledMessagesRepository(repository: ScheduledMessageRepositoryImpl): ScheduledMessageRepository =
        repository

    @Provides
    fun provideSyncRepository(repository: SyncRepositoryImpl): SyncRepository = repository

}