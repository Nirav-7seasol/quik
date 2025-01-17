
package com.messages.readmms.readsmss.common

import android.app.Activity
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Telephony
import com.messages.readmms.readsmss.feature.language.LanguageSelectionActivity
import com.messages.readmms.readsmss.BuildConfig
import com.messages.readmms.readsmss.feature.backup.BackupActivity
import com.messages.readmms.readsmss.feature.blocking.BlockingActivity
import com.messages.readmms.readsmss.feature.compose.ComposeActivity
import com.messages.readmms.readsmss.feature.conversationinfo.ConversationInfoActivity
import com.messages.readmms.readsmss.feature.gallery.GalleryActivity
import com.messages.readmms.readsmss.feature.notificationprefs.NotificationPrefsActivity
import com.messages.readmms.readsmss.feature.scheduled.ScheduledActivity
import com.messages.readmms.readsmss.feature.settings.SettingsActivity
import com.messages.readmms.readsmss.manager.NotificationManager
import com.messages.readmms.readsmss.manager.PermissionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val permissions: PermissionManager
) {

    private fun startActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun startActivityExternal(intent: Intent) {
        if (intent.resolveActivity(context.packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent.createChooser(intent, null))
        }
    }

    /**
     * @param source String to indicate where this QKSMS+ screen was launched from. This should be
     * one of [main_menu, compose_schedule, settings_night, settings_theme]
     */
    fun showQksmsPlusActivity(source: String) {
//        analyticsManager.track("Viewed QKSMS+", Pair("source", source))
//        val intent = Intent(context, PlusActivity::class.java)
//        startActivity(intent)
    }

    /**
     * This won't work unless we use startActivityForResult
     */
    fun showDefaultSmsDialog(context: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            context.startActivityForResult(intent, 42389)
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.packageName)
            context.startActivity(intent)
        }
    }

    fun showCompose(body: String? = null, images: List<Uri>? = null) {
        val intent = Intent(context, ComposeActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, body)

        images?.takeIf { it.isNotEmpty() }?.let {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(images))
        }

        startActivity(intent)
    }

    fun showConversation(threadId: Long, query: String? = null) {
        val intent = Intent(context, ComposeActivity::class.java)
                .putExtra("threadId", threadId)
                .putExtra("query", query)
        startActivity(intent)
    }

    fun showConversationWithAd(threadId: Long, isAdShow: Boolean = false) {
        val intent = Intent(context, ComposeActivity::class.java).putExtra("threadId", threadId)
            .putExtra("isAdShow", isAdShow)
        intent.putExtra("ComAdShow", true)
        startActivity(intent)
    }

    fun showConversationInfo(threadId: Long) {
        val intent = Intent(context, ConversationInfoActivity::class.java)
        intent.putExtra("threadId", threadId)
        startActivity(intent)
    }

    fun showMedia(partId: Long) {
        val intent = Intent(context, GalleryActivity::class.java)
        intent.putExtra("partId", partId)
        startActivity(intent)
    }

    fun showBackup() {
//        analyticsManager.track("Viewed Backup")
        startActivity(Intent(context, BackupActivity::class.java))
    }

    fun showScheduled() {
//        analyticsManager.track("Viewed Scheduled")
        val intent = Intent(context, ScheduledActivity::class.java)
        startActivity(intent)
    }

    fun showSettings() {
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun showDeveloper() {
    }

    fun showSourceCode() {

    }

    fun showChangelog() {
    }

    fun showLicense() {
    }

    fun showBlockedConversations() {
        val intent = Intent(context, BlockingActivity::class.java)
        startActivity(intent)
    }

    fun makePhoneCall(address: String) {
        val action = if (permissions.hasCalling()) Intent.ACTION_CALL else Intent.ACTION_DIAL
        val intent = Intent(action, Uri.parse("tel:$address"))
        startActivityExternal(intent)
    }

    fun showDonation() {
    }

    fun showRating() {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                .addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY
                            or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                            or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )

        try {
            startActivityExternal(intent)
        } catch (e: ActivityNotFoundException) {
            val url = "https://play.google.com/store/apps/details?id=${context.packageName}"
            startActivityExternal(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    /**
     * Launch the Play Store and display the Call Blocker listing
     */
    fun installCallBlocker() {
        val url = "https://play.google.com/store/apps/details?id=com.cuiet.blockCalls"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivityExternal(intent)
    }

    /**
     * Launch the Play Store and display the Call Control listing
     */
    fun installCallControl() {
        val url = "https://play.google.com/store/apps/details?id=com.flexaspect.android.everycallcontrol"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivityExternal(intent)
    }

    /**
     * Launch the Play Store and display the Should I Answer? listing
     */
    fun installSia() {
        val url = "https://play.google.com/store/apps/details?id=org.mistergroup.shouldianswer"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivityExternal(intent)
    }

    fun showSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("quik@octo.sh"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "QUIK Support")
        intent.putExtra(Intent.EXTRA_TEXT, StringBuilder("\n\n")
                .append("\n\n--- Please write your message above this line ---\n\n")
                .append("Package: ${context.packageName}\n")
                .append("Version: ${BuildConfig.VERSION_NAME}\n")
                .append("Device: ${Build.BRAND} ${Build.MODEL}\n")
                .append("SDK: ${Build.VERSION.SDK_INT}\n")
                .toString())
        startActivityExternal(intent)
    }

    fun showInvite() {
//        analyticsManager.track("Clicked Invite")
        Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, "https://github.com/octoshrimpy/quik/releases/latest")
                .let { Intent.createChooser(it, null) }
                .let(::startActivityExternal)
    }

    fun showLanguageSelectionScreen() {
        val intent = Intent(context, LanguageSelectionActivity::class.java)
        intent.putExtra("FROM_SETTING", true)
        startActivity(intent)
    }

    fun addContact(address: String) {
        val intent = Intent(Intent.ACTION_INSERT)
                .setType(ContactsContract.Contacts.CONTENT_TYPE)
                .putExtra(ContactsContract.Intents.Insert.PHONE, address)

        startActivityExternal(intent)
    }

    fun showContact(lookupKey: String) {
        val intent = Intent(Intent.ACTION_VIEW)
                .setData(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey))

        startActivityExternal(intent)
    }

    fun viewFile(uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, mimeType.lowercase())
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .let { Intent.createChooser(it, null) }

        startActivityExternal(intent)
    }

    fun shareFile(uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_SEND)
                .setType(mimeType.lowercase())
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .let { Intent.createChooser(it, null) }

        startActivityExternal(intent)
    }

    fun showPermissions() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", context.packageName, null)

        startActivity(intent)
    }

    fun showNotificationSettings(threadId: Long = 0) {
        val intent = Intent(context, NotificationPrefsActivity::class.java)
        intent.putExtra("threadId", threadId)
        startActivity(intent)
    }

    fun showNotificationChannel(threadId: Long = 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (threadId != 0L) {
                notificationManager.createNotificationChannel(threadId)
            }

            val channelId = notificationManager.buildNotificationChannelId(threadId)
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            startActivity(intent)
        }
    }

    fun showExactAlarmsSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .setData(Uri.parse("package:${context.packageName}"))
            startActivity(intent)
        }
    }

}
