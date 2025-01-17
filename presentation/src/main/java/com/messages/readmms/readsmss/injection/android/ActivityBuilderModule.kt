
package com.messages.readmms.readsmss.injection.android

import com.messages.readmms.readsmss.feature.language.LanguageSelectionActivity
import com.messages.readmms.readsmss.feature.permission.PermissionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.messages.readmms.readsmss.feature.backup.BackupActivity
import com.messages.readmms.readsmss.feature.blocking.BlockingActivity
import com.messages.readmms.readsmss.feature.compose.ComposeActivity
import com.messages.readmms.readsmss.feature.compose.ComposeActivityModule
import com.messages.readmms.readsmss.feature.contacts.ContactsActivity
import com.messages.readmms.readsmss.feature.contacts.ContactsActivityModule
import com.messages.readmms.readsmss.feature.conversationinfo.ConversationInfoActivity
import com.messages.readmms.readsmss.feature.gallery.GalleryActivity
import com.messages.readmms.readsmss.feature.gallery.GalleryActivityModule
import com.messages.readmms.readsmss.feature.main.MainActivity
import com.messages.readmms.readsmss.feature.main.MainActivityModule
import com.messages.readmms.readsmss.feature.notificationprefs.NotificationPrefsActivity
import com.messages.readmms.readsmss.feature.notificationprefs.NotificationPrefsActivityModule
import com.messages.readmms.readsmss.feature.qkreply.QkReplyActivity
import com.messages.readmms.readsmss.feature.qkreply.QkReplyActivityModule
import com.messages.readmms.readsmss.feature.scheduled.ScheduledActivity
import com.messages.readmms.readsmss.feature.scheduled.ScheduledActivityModule
import com.messages.readmms.readsmss.feature.settings.SettingsActivity
import com.messages.readmms.readsmss.injection.scope.ActivityScope

@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindPermissionActivity(): PermissionActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindLanguageSelectionActivity(): LanguageSelectionActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindBackupActivity(): BackupActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ComposeActivityModule::class])
    abstract fun bindComposeActivity(): ComposeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ContactsActivityModule::class])
    abstract fun bindContactsActivity(): ContactsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindConversationInfoActivity(): ConversationInfoActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [GalleryActivityModule::class])
    abstract fun bindGalleryActivity(): GalleryActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [NotificationPrefsActivityModule::class])
    abstract fun bindNotificationPrefsActivity(): NotificationPrefsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [QkReplyActivityModule::class])
    abstract fun bindQkReplyActivity(): QkReplyActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ScheduledActivityModule::class])
    abstract fun bindScheduledActivity(): ScheduledActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindSettingsActivity(): SettingsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindBlockingActivity(): BlockingActivity

}
