
package com.messages.readmms.readsmss.feature.blocking.numbers

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding2.view.clicks
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.base.QkController
import com.messages.readmms.readsmss.common.util.Colors
import com.messages.readmms.readsmss.common.util.extensions.setBackgroundTint
import com.messages.readmms.readsmss.common.util.extensions.setTint
import com.messages.readmms.readsmss.injection.appComponent
import com.messages.readmms.readsmss.util.PhoneNumberUtils
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.blocked_numbers_add_dialog.view.*
import kotlinx.android.synthetic.main.blocked_numbers_controller.*
import javax.inject.Inject

class BlockedNumbersController : QkController<BlockedNumbersView, BlockedNumbersState, BlockedNumbersPresenter>(),
    BlockedNumbersView {

    @Inject override lateinit var presenter: BlockedNumbersPresenter
    @Inject lateinit var colors: Colors
    @Inject lateinit var phoneNumberUtils: PhoneNumberUtils

    private val adapter = BlockedNumbersAdapter()
    private val saveAddressSubject: Subject<String> = PublishSubject.create()

    init {
        appComponent.inject(this)
        retainViewMode = RetainViewMode.RETAIN_DETACH
        layoutRes = R.layout.blocked_numbers_controller
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.blocked_numbers_title)
        showBackButton(true)
    }

    override fun onViewCreated() {
        super.onViewCreated()
        add.setBackgroundTint(colors.theme().theme)
        add.setTint(colors.theme().textPrimary)
        adapter.emptyView = empty
        numbers.adapter = adapter
    }

    override fun render(state: BlockedNumbersState) {
        adapter.updateData(state.numbers)
    }

    override fun unblockAddress(): Observable<Long> = adapter.unblockAddress
    override fun addAddress(): Observable<*> = add.clicks()
    override fun saveAddress(): Observable<String> = saveAddressSubject

    override fun showAddDialog() {
        val layout = LayoutInflater.from(activity).inflate(R.layout.blocked_numbers_add_dialog, null)
        val textWatcher = BlockedNumberTextWatcher(layout.input, phoneNumberUtils)
        val dialog = MaterialAlertDialogBuilder(activity!!)
                .setView(layout)
                .setPositiveButton(R.string.blocked_numbers_dialog_block) { _, _ ->
                    saveAddressSubject.onNext(layout.input.text.toString())
                }
                .setNegativeButton(R.string.button_cancel) { _, _ -> }
                .setOnDismissListener { textWatcher.dispose() }
        dialog.show()
    }

}
