package dev.octoshrimpy.quik.feature.compose

import android.Manifest
import android.animation.LayoutTransition
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.moez.QKSMS.common.hide
import com.moez.QKSMS.common.show
import com.moez.QKSMS.myadsworld.MyAddPrefs
import com.moez.QKSMS.myadsworld.MyAllAdCommonClass.SmallNativeBannerLoad
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import dagger.android.AndroidInjection
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.common.Navigator
import dev.octoshrimpy.quik.common.base.QkThemedActivity
import dev.octoshrimpy.quik.common.util.DateFormatter
import dev.octoshrimpy.quik.common.util.extensions.autoScrollToStart
import dev.octoshrimpy.quik.common.util.extensions.hideKeyboard
import dev.octoshrimpy.quik.common.util.extensions.scrapViews
import dev.octoshrimpy.quik.common.util.extensions.setBackgroundTint
import dev.octoshrimpy.quik.common.util.extensions.setTint
import dev.octoshrimpy.quik.common.util.extensions.setVisible
import dev.octoshrimpy.quik.common.util.extensions.showKeyboard
import dev.octoshrimpy.quik.common.util.extensions.viewBinding
import dev.octoshrimpy.quik.databinding.ComposeActivityBinding
import dev.octoshrimpy.quik.feature.compose.editing.ChipsAdapter
import dev.octoshrimpy.quik.feature.contacts.ContactsActivity
import dev.octoshrimpy.quik.model.Attachment
import dev.octoshrimpy.quik.model.Recipient
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.compose_activity.sendAsGroupBackground
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ComposeActivity : QkThemedActivity(), ComposeView {

    private val binding by viewBinding(ComposeActivityBinding::inflate)

    companion object {
        private const val SelectContactRequestCode = 0
        private const val TakePhotoRequestCode = 1
        private const val AttachPhotoRequestCode = 2
        private const val AttachContactRequestCode = 3

        private const val CameraDestinationKey = "camera_destination"
    }

    @Inject lateinit var attachmentAdapter: AttachmentAdapter
    @Inject lateinit var chipsAdapter: ChipsAdapter
    @Inject lateinit var dateFormatter: DateFormatter
    @Inject lateinit var messageAdapter: MessagesAdapter
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override val activityVisibleIntent: Subject<Boolean> = PublishSubject.create()
    override val chipsSelectedIntent: Subject<HashMap<String, String?>> = PublishSubject.create()
    override val chipDeletedIntent: Subject<Recipient> by lazy { chipsAdapter.chipDeleted }
    override val menuReadyIntent: Observable<Unit> = menu.map { Unit }
    override val optionsItemIntent: Subject<Int> = PublishSubject.create()
    override val sendAsGroupIntent by lazy { sendAsGroupBackground.clicks() }
    override val messageClickIntent: Subject<Long> by lazy { messageAdapter.clicks }
    override val messagePartClickIntent: Subject<Long> by lazy { messageAdapter.partClicks }
    override val messagesSelectedIntent by lazy { messageAdapter.selectionChanges }
    override val cancelSendingIntent: Subject<Long> by lazy { messageAdapter.cancelSending }
    override val attachmentDeletedIntent: Subject<Attachment> by lazy { attachmentAdapter.attachmentDeleted }
    override val textChangedIntent by lazy { binding.message.textChanges() }
    override val attachIntent by lazy { Observable.merge(binding.attach.clicks(), binding.attachingBackground.clicks()) }
    override val cameraIntent by lazy { Observable.merge(binding.camera.clicks(), binding.cameraLabel.clicks()) }
    override val galleryIntent by lazy { Observable.merge(binding.gallery.clicks(), binding.galleryLabel.clicks()) }
    override val scheduleIntent by lazy { Observable.merge(binding.schedule.clicks(), binding.scheduleLabel.clicks()) }
    override val attachContactIntent by lazy { Observable.merge(binding.contact.clicks(), binding.contactLabel.clicks()) }
    override val attachmentSelectedIntent: Subject<Uri> = PublishSubject.create()
    override val contactSelectedIntent: Subject<Uri> = PublishSubject.create()
    override val inputContentIntent by lazy { binding.message.inputContentSelected }
    override val scheduleSelectedIntent: Subject<Long> = PublishSubject.create()
    override val changeSimIntent by lazy { binding.sim.clicks() }
    override val scheduleCancelIntent by lazy { binding.scheduledCancel.clicks() }
    override val sendIntent by lazy { binding.send.clicks() }
    override val viewQksmsPlusIntent: Subject<Unit> = PublishSubject.create()
    override val backPressedIntent: Subject<Unit> = PublishSubject.create()
    override val confirmDeleteIntent: Subject<List<Long>> = PublishSubject.create()

    private var isAdShow: Boolean = false
    private var ComAdShow: Boolean = false

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[ComposeViewModel::class.java] }

    private var cameraDestination: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        showBackButton(true)
        viewModel.bindView(this)

        ComAdShow = intent.getBooleanExtra("ComAdShow", false)
        isAdShow = intent.getBooleanExtra("isAdShow", false)
        Log.e("TAG111", "onCreate: ComAdShow::$ComAdShow")
        Log.e("TAG111", "onCreate:isAdShow:: $isAdShow")

        loadAds()

        binding.contentView.layoutTransition = LayoutTransition().apply {
            disableTransitionType(LayoutTransition.CHANGING)
        }

        chipsAdapter.view = binding.chips

        binding.chips.itemAnimator = null
        binding.chips.layoutManager = FlexboxLayoutManager(this)

        messageAdapter.autoScrollToStart(binding.messageList)
        messageAdapter.emptyView = binding.messagesEmpty

        binding.messageList.setHasFixedSize(true)
        binding.messageList.adapter = messageAdapter

        binding.attachments.adapter = attachmentAdapter

        binding.message.supportsInputContent = true

        theme
            .doOnNext { binding.loading.setTint(it.theme) }
            .doOnNext { binding.attach.setBackgroundTint(it.theme) }
            .doOnNext { binding.attach.setTint(it.textPrimary) }
            .doOnNext { messageAdapter.theme = it }
            .autoDisposable(scope())
            .subscribe()

        window.callback = ComposeWindowCallback(window.callback, this)

        binding.contentView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.contentView.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.contentView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            // Check if the keyboard is open
            if (keypadHeight > screenHeight * 0.15 || binding.attaching.isVisible) {
                binding.adRl1.hide()
            } else {
                if (!isAdShow && !binding.attaching.isVisible && ComAdShow) {
                    binding.adRl1.show()
                }
            }
        }

        binding.cardNoreply.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setMessage(R.string.str_notreply_dis)
                .setPositiveButton("Ok") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        }
    }

    private fun loadAds() {
        if (ComAdShow) {
            if (isAdShow) {
                Log.e("TAG111", "loadAds: isAdShow" )
                SmallNativeBannerLoad(
                    this,
                    binding.myTemplate2,
                    binding.shimmerViewContainer,
                    MyAddPrefs(this).admNativeId,
                    colors.theme().theme
                )
                binding.adRl1.visibility = View.GONE
                binding.adRl.visibility = View.VISIBLE
            } else {
                binding.adRl1.visibility = View.VISIBLE
                binding.adRl.visibility = View.GONE
                SmallNativeBannerLoad(
                    this,
                    binding.myTemplate1,
                    binding.shimmerViewContainer1,
                    MyAddPrefs(this).admNativeId,
                    colors.theme().theme
                )
            }
        } else {
            binding.adRl.visibility = View.GONE
            binding.adRl1.visibility = View.GONE
            Log.e("TAG111", "loadAds: else" )
        }
    }

    override fun onStart() {
        super.onStart()
        activityVisibleIntent.onNext(true)
    }

    override fun onPause() {
        super.onPause()
        activityVisibleIntent.onNext(false)
    }

    override fun render(state: ComposeState) {
        if (state.hasError) {
            finish()
            return
        }

//        threadId.onNext(state.threadId)

        title = when {
            state.selectedMessages > 0 -> getString(R.string.compose_title_selected, state.selectedMessages)
            state.query.isNotEmpty() -> state.query
            else -> state.conversationtitle
        }

        binding.toolbarSubtitle.setVisible(state.query.isNotEmpty())
        binding.toolbarSubtitle.text = getString(R.string.compose_subtitle_results, state.searchSelectionPosition,
            state.searchResults)

        toolbarTitle.setVisible(!state.editingMode)
        binding.chips.setVisible(state.editingMode)
        binding.composeBar.setVisible(!state.loading)

        // Don't set the adapters unless needed
        if (state.editingMode && binding.chips.adapter == null) binding.chips.adapter = chipsAdapter

        toolbar?.menu?.findItem(R.id.add)?.isVisible = state.editingMode
        toolbar?.menu?.findItem(R.id.call)?.isVisible = !state.editingMode && state.selectedMessages == 0
                && state.query.isEmpty()
        toolbar?.menu?.findItem(R.id.info)?.isVisible = !state.editingMode && state.selectedMessages == 0
                && state.query.isEmpty()
        toolbar?.menu?.findItem(R.id.copy)?.isVisible = !state.editingMode && state.selectedMessages > 0
        toolbar?.menu?.findItem(R.id.details)?.isVisible = !state.editingMode && state.selectedMessages == 1
        toolbar?.menu?.findItem(R.id.delete)?.isVisible = !state.editingMode && state.selectedMessages > 0
        toolbar?.menu?.findItem(R.id.forward)?.isVisible = !state.editingMode && state.selectedMessages == 1
        toolbar?.menu?.findItem(R.id.previous)?.isVisible = state.selectedMessages == 0 && state.query.isNotEmpty()
        toolbar?.menu?.findItem(R.id.next)?.isVisible = state.selectedMessages == 0 && state.query.isNotEmpty()
        toolbar?.menu?.findItem(R.id.clear)?.isVisible = state.selectedMessages == 0 && state.query.isNotEmpty()

        binding.composeBar.setVisible(!state.loading && state.conversationEnable)
        binding.attach.setVisible(!state.loading && state.conversationEnable)
        binding.relNoreply.setVisible(!state.loading && !state.conversationEnable)

        if (binding.relNoreply.visibility == View.GONE) {
            binding.message.hint = (getString(R.string.compose_hint))
        } else {
            binding.message.hint = ""
        }

        chipsAdapter.data = state.selectedChips

        binding.loading.setVisible(state.loading)

        binding.sendAsGroup.setVisible(state.editingMode && state.selectedChips.size >= 2)
        binding.sendAsGroupSwitch.isChecked = state.sendAsGroup

        binding.messageList.setVisible(!state.editingMode || state.sendAsGroup || state.selectedChips.size == 1)
        messageAdapter.data = state.messages
        messageAdapter.highlight = state.searchSelectionId

        binding.scheduledGroup.isVisible = state.scheduled != 0L
        binding.scheduledTime.text = dateFormatter.getScheduledTimestamp(state.scheduled)

        binding.attachments.setVisible(state.attachments.isNotEmpty())
        attachmentAdapter.data = state.attachments

        binding.attach.animate().rotation(if (state.attaching) 135f else 0f).start()
        binding.attaching.isVisible = state.attaching

        binding.counter.text = state.remaining
        binding.counter.setVisible(binding.counter.text.isNotBlank())

        binding.sim.setVisible(state.subscription != null)
        binding.sim.contentDescription = getString(R.string.compose_sim_cd, state.subscription?.displayName)
        binding.simIndex.text = state.subscription?.simSlotIndex?.plus(1)?.toString()

        binding.send.isEnabled = state.canSend
        binding.send.imageAlpha = if (state.canSend) 255 else 128
    }

    override fun clearSelection() = messageAdapter.clearSelection()

    override fun showDetails(details: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.compose_details_title)
            .setMessage(details)
            .setCancelable(true)
            .show()
    }

    override fun requestDefaultSms() {
        navigator.showDefaultSmsDialog(this)
    }

    override fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
    }

    override fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS), 0)
    }

    override fun requestDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                scheduleSelectedIntent.onNext(calendar.timeInMillis)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this))
                .show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()

        // On some devices, the keyboard can cover the date picker
        binding.message.hideKeyboard()
    }

    override fun requestContact() {
        val intent = Intent(Intent.ACTION_PICK)
            .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE)

        startActivityForResult(Intent.createChooser(intent, null), AttachContactRequestCode)
    }

    override fun showContacts(sharing: Boolean, chips: List<Recipient>) {
        binding.message.hideKeyboard()
        val serialized = HashMap(chips.associate { chip -> chip.address to chip.contact?.lookupKey })
        val intent = Intent(this, ContactsActivity::class.java)
            .putExtra(ContactsActivity.SharingKey, sharing)
            .putExtra(ContactsActivity.ChipsKey, serialized)
        startActivityForResult(intent, SelectContactRequestCode)
    }

    override fun themeChanged() {
        binding.messageList.scrapViews()
    }

    override fun showKeyboard() {
        binding.message.postDelayed({
            binding.message.showKeyboard()
        }, 200)
    }

    override fun requestCamera() {
        cameraDestination = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            .let { timestamp -> ContentValues().apply { put(MediaStore.Images.Media.TITLE, timestamp) } }
            .let { cv -> contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv) }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, cameraDestination)
        startActivityForResult(Intent.createChooser(intent, null), TakePhotoRequestCode)
    }

    override fun requestGallery() {
        val intent = Intent(Intent.ACTION_PICK)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            .putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setType("image/*")
        startActivityForResult(Intent.createChooser(intent, null), AttachPhotoRequestCode)
    }

    override fun setDraft(draft: String) {
        binding.message.setText(draft)
        binding.message.setSelection(draft.length)
    }

    override fun scrollToMessage(id: Long) {
        messageAdapter.data?.second
            ?.indexOfLast { message -> message.id == id }
            ?.takeIf { position -> position != -1 }
            ?.let(binding.messageList::scrollToPosition)
    }

    override fun showQksmsPlusSnackbar(message: Int) {
        Snackbar.make(binding.contentView, message, Snackbar.LENGTH_LONG).run {
            setAction(R.string.button_more) { viewQksmsPlusIntent.onNext(Unit) }
            setActionTextColor(colors.theme().theme)
            show()
        }
    }

    override fun showDeleteDialog(messages: List<Long>) {
        val count = messages.size
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(resources.getQuantityString(R.plurals.dialog_delete_chat, count, count))
            .setPositiveButton(R.string.button_delete) { _, _ -> confirmDeleteIntent.onNext(messages) }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.compose, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        optionsItemIntent.onNext(item.itemId)
        return true
    }

    override fun getColoredMenuItems(): List<Int> {
        return super.getColoredMenuItems() + R.id.call
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == SelectContactRequestCode -> {
                chipsSelectedIntent.onNext(data?.getSerializableExtra(ContactsActivity.ChipsKey)
                    ?.let { serializable -> serializable as? HashMap<String, String?> }
                    ?: hashMapOf())
            }
            requestCode == TakePhotoRequestCode && resultCode == Activity.RESULT_OK -> {
                cameraDestination?.let(attachmentSelectedIntent::onNext)
            }
            requestCode == AttachPhotoRequestCode && resultCode == Activity.RESULT_OK -> {
                data?.clipData?.itemCount
                    ?.let { count -> 0 until count }
                    ?.mapNotNull { i -> data.clipData?.getItemAt(i)?.uri }
                    ?.forEach(attachmentSelectedIntent::onNext)
                    ?: data?.data?.let(attachmentSelectedIntent::onNext)
            }
            requestCode == AttachContactRequestCode && resultCode == Activity.RESULT_OK -> {
                data?.data?.let(contactSelectedIntent::onNext)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CameraDestinationKey, cameraDestination)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        cameraDestination = savedInstanceState.getParcelable(CameraDestinationKey)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() = backPressedIntent.onNext(Unit)

}