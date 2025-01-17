
package com.messages.readmms.readsmss.injection.android

import com.messages.readmms.readsmss.feature.backup.RestoreBackupService
import com.messages.readmms.readsmss.injection.scope.ActivityScope
import com.messages.readmms.readsmss.service.HeadlessSmsSendService
import com.messages.readmms.readsmss.receiver.SendSmsReceiver
import com.messages.readmms.readsmss.service.AutoDeleteService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAutoDeleteService(): AutoDeleteService

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindHeadlessSmsSendService(): HeadlessSmsSendService

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindRestoreBackupService(): RestoreBackupService

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSendSmsReceiver(): SendSmsReceiver

}
