
package com.messages.readmms.readsmss.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.messages.readmms.readsmss.receiver.SendScheduledMessageReceiver
import javax.inject.Inject

class AlarmManagerImpl @Inject constructor(private val context: Context) : AlarmManager {

    override fun getScheduledMessageIntent(id: Long): PendingIntent {
        val intent = Intent(context, SendScheduledMessageReceiver::class.java).putExtra("id", id)
        return PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun setAlarm(date: Long, intent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, date, intent)
    }

}