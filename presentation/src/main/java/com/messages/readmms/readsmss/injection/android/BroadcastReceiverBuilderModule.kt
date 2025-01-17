
package com.messages.readmms.readsmss.injection.android

import com.messages.readmms.readsmss.feature.widget.WidgetProvider
import com.messages.readmms.readsmss.injection.scope.ActivityScope
import com.messages.readmms.readsmss.receiver.BlockThreadReceiver
import com.messages.readmms.readsmss.receiver.BootReceiver
import com.messages.readmms.readsmss.receiver.DefaultSmsChangedReceiver
import com.messages.readmms.readsmss.receiver.DeleteMessagesReceiver
import com.messages.readmms.readsmss.receiver.MarkArchivedReceiver
import com.messages.readmms.readsmss.receiver.MarkReadReceiver
import com.messages.readmms.readsmss.receiver.MarkSeenReceiver
import com.messages.readmms.readsmss.receiver.MmsReceivedReceiver
import com.messages.readmms.readsmss.receiver.MmsReceiver
import com.messages.readmms.readsmss.receiver.MmsSentReceiver
import com.messages.readmms.readsmss.receiver.MmsUpdatedReceiver
import com.messages.readmms.readsmss.receiver.NightModeReceiver
import com.messages.readmms.readsmss.receiver.RemoteMessagingReceiver
import com.messages.readmms.readsmss.receiver.SendScheduledMessageReceiver
import com.messages.readmms.readsmss.receiver.SmsDeliveredReceiver
import com.messages.readmms.readsmss.receiver.SmsProviderChangedReceiver
import com.messages.readmms.readsmss.receiver.SmsReceiver
import com.messages.readmms.readsmss.receiver.SmsSentReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindBlockThreadReceiver(): BlockThreadReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindBootReceiver(): BootReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindDefaultSmsChangedReceiver(): DefaultSmsChangedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindDeleteMessagesReceiver(): DeleteMessagesReceiver

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindMarkArchivedReceiver(): MarkArchivedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMarkReadReceiver(): MarkReadReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMarkSeenReceiver(): MarkSeenReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsReceivedReceiver(): MmsReceivedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsReceiver(): MmsReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsSentReceiver(): MmsSentReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMmsUpdatedReceiver(): MmsUpdatedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindNightModeReceiver(): NightModeReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindRemoteMessagingReceiver(): RemoteMessagingReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSendScheduledMessageReceiver(): SendScheduledMessageReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsDeliveredReceiver(): SmsDeliveredReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsProviderChangedReceiver(): SmsProviderChangedReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsReceiver(): SmsReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSmsSentReceiver(): SmsSentReceiver

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindWidgetProvider(): WidgetProvider

}