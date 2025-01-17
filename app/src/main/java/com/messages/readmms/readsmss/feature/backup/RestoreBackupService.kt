
package com.messages.readmms.readsmss.feature.backup

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.messages.readmms.readsmss.common.util.extensions.getLabel
import com.messages.readmms.readsmss.manager.NotificationManager
import com.messages.readmms.readsmss.repository.BackupRepository
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RestoreBackupService : Service() {

    companion object {
        private const val NOTIFICATION_ID = -1

        private const val ACTION_START = "com.messages.readmms.readsmss.ACTION_START"
        private const val ACTION_STOP = "com.messages.readmms.readsmss.ACTION_STOP"
        private const val EXTRA_FILE_PATH = "com.messages.readmms.readsmss.EXTRA_FILE_PATH"

        fun start(context: Context, filePath: String) {
            val intent = Intent(context, RestoreBackupService::class.java)
                .setAction(ACTION_START)
                .putExtra(EXTRA_FILE_PATH, filePath)

            ContextCompat.startForegroundService(context, intent)
        }
    }

    @Inject
    lateinit var backupRepo: BackupRepository
    @Inject
    lateinit var notificationManager: NotificationManager

    private val notification by lazy { notificationManager.getNotificationForBackup() }

    override fun onCreate() = AndroidInjection.inject(this)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent.action) {
            ACTION_START -> start(intent)
            ACTION_STOP -> stop()
        }

        return Service.START_STICKY
    }

    @SuppressLint("CheckResult")
    private fun start(intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) { // Android 14
            startForeground(
                NOTIFICATION_ID,
                notification.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            );
        } else {
            startForeground(NOTIFICATION_ID, notification.build())
        }

        backupRepo.getRestoreProgress()
            .sample(200, TimeUnit.MILLISECONDS, true)
            .subscribeOn(Schedulers.io())
            .subscribe { progress ->
                when (progress) {
                    is BackupRepository.Progress.Idle -> stop()

                    is BackupRepository.Progress.Running -> notification
                        .setProgress(progress.max, progress.count, progress.indeterminate)
                        .setContentText(progress.getLabel(this))
                        .let { notificationManager.notify(NOTIFICATION_ID, it.build()) }

                    else -> notification
                        .setProgress(0, 0, progress.indeterminate)
                        .setContentText(progress.getLabel(this))
                        .let { notificationManager.notify(NOTIFICATION_ID, it.build()) }
                }
            }

        // Start the restore
        Observable.just(intent)
            .map { it.getStringExtra(EXTRA_FILE_PATH) }
            .map(backupRepo::performRestore)
            .subscribeOn(Schedulers.io())
            .subscribe({}, Timber::w)
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

}