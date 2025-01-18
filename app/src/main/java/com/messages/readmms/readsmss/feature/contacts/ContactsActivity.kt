
package com.messages.readmms.readsmss.feature.contacts

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActions
import com.jakewharton.rxbinding2.widget.textChanges
import dagger.android.AndroidInjection
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.ViewModelFactory
import com.messages.readmms.readsmss.common.base.QkThemedActivity
import com.messages.readmms.readsmss.common.util.extensions.hideKeyboard
import com.messages.readmms.readsmss.common.util.extensions.showKeyboard
import com.messages.readmms.readsmss.common.util.extensions.viewBinding
import com.messages.readmms.readsmss.common.widget.MyDialog
import com.messages.readmms.readsmss.databinding.ContactsActivityBinding
import com.messages.readmms.readsmss.extensions.Optional
import com.messages.readmms.readsmss.feature.compose.editing.ComposeItem
import com.messages.readmms.readsmss.feature.compose.editing.ComposeItemAdapter
import com.messages.readmms.readsmss.feature.compose.editing.PhoneNumberAction
import com.messages.readmms.readsmss.feature.compose.editing.PhoneNumberPickerAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject
import com.messages.readmms.readsmss.myadsworld.MyAllAdCommonClass.SmallNativeBannerLoad
import com.messages.readmms.readsmss.myadsworld.MyAddPrefs

class ContactsActivity : QkThemedActivity(), ContactsContract {

    companion object {
        const val SharingKey = "sharing"
        const val ChipsKey = "chips"
    }

    @Inject lateinit var contactsAdapter: ComposeItemAdapter
    @Inject lateinit var phoneNumberAdapter: PhoneNumberPickerAdapter
    @Inject lateinit var viewModelFactory: ViewModelFactory

    override val queryChangedIntent: Observable<CharSequence> by lazy { binding.search.textChanges() }
    override val queryClearedIntent: Observable<*> by lazy { binding.cancel.clicks() }
    override val queryEditorActionIntent: Observable<Int> by lazy { binding.search.editorActions() }
    override val composeItemPressedIntent: Subject<ComposeItem> by lazy { contactsAdapter.clicks }
    override val composeItemLongPressedIntent: Subject<ComposeItem> by lazy { contactsAdapter.longClicks }
    override val phoneNumberSelectedIntent: Subject<Optional<Long>> by lazy { phoneNumberAdapter.selectedItemChanges }
    override val phoneNumberActionIntent: Subject<PhoneNumberAction> = PublishSubject.create()

    private val binding by viewBinding(ContactsActivityBinding::inflate)
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[ContactsViewModel::class.java] }

    private val phoneNumberDialog by lazy {
        MyDialog(this).apply {
            titleRes = R.string.compose_number_picker_title
            adapter = phoneNumberAdapter
            positiveButton = R.string.compose_number_picker_always
            positiveButtonListener = { phoneNumberActionIntent.onNext(PhoneNumberAction.ALWAYS) }
            negativeButton = R.string.compose_number_picker_once
            negativeButtonListener = { phoneNumberActionIntent.onNext(PhoneNumberAction.JUST_ONCE) }
            cancelListener = { phoneNumberActionIntent.onNext(PhoneNumberAction.CANCEL) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.shimmerViewContainer.startShimmer()
        SmallNativeBannerLoad(
            this,
            binding.myTemplate,
            binding.shimmerViewContainer,
            MyAddPrefs(this).admNativeId,
            colors.theme().theme
        )

        binding.contentView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.contentView.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.contentView.rootView.height
            val keypadHeight = screenHeight - r.bottom
            binding.adRl2.isVisible = (keypadHeight > screenHeight * 0.15).not()
        }

        showBackButton(true)
        viewModel.bindView(this)

        binding.contacts.adapter = contactsAdapter

        // These theme attributes don't apply themselves on API 21
    }

    override fun render(state: ContactsState) {
        binding.cancel.isVisible = state.query.length > 1

        contactsAdapter.data = state.composeItems

        if (state.selectedContact != null && !phoneNumberDialog.isShowing) {
            phoneNumberAdapter.data = state.selectedContact.numbers
            phoneNumberDialog.subtitle = state.selectedContact.name
            phoneNumberDialog.show()
        } else if (state.selectedContact == null && phoneNumberDialog.isShowing) {
            phoneNumberDialog.dismiss()
        }
    }

    override fun clearQuery() {
        binding.search.text = null
    }

    override fun openKeyboard() {
        binding.search.postDelayed({
            binding.search.showKeyboard()
        }, 200)
    }

    override fun finish(result: HashMap<String, String?>) {
        binding.search.hideKeyboard()
        val intent = Intent().putExtra(ChipsKey, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
