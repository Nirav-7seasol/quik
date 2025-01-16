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
package dev.octoshrimpy.quik.feature.conversationinfo

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moez.QKSMS.common.base.QkControllerWithBinding
import com.moez.QKSMS.common.widget.FieldDialog
import com.moez.QKSMS.myadsworld.MyAddPrefs
import com.moez.QKSMS.myadsworld.MyAllAdCommonClass.SmallNativeBannerLoad
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.common.Navigator
import dev.octoshrimpy.quik.common.QkChangeHandler
import dev.octoshrimpy.quik.common.util.Colors
import dev.octoshrimpy.quik.common.util.extensions.scrapViews
import dev.octoshrimpy.quik.databinding.ConversationInfoControllerBinding
import dev.octoshrimpy.quik.feature.blocking.BlockingDialog
import dev.octoshrimpy.quik.feature.conversationinfo.injection.ConversationInfoModule
import dev.octoshrimpy.quik.feature.themepicker.ThemePickerController
import dev.octoshrimpy.quik.injection.appComponent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class ConversationInfoController(
    val threadId: Long = 0
) : QkControllerWithBinding<ConversationInfoView, ConversationInfoState, ConversationInfoPresenter,
        ConversationInfoControllerBinding>(ConversationInfoControllerBinding::inflate),
    ConversationInfoView {

    @Inject
    override lateinit var presenter: ConversationInfoPresenter
    @Inject
    lateinit var blockingDialog: BlockingDialog
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var adapter: ConversationInfoAdapter

    @Inject
    lateinit var colors: Colors

    private val nameDialog: FieldDialog by lazy {
        FieldDialog(activity!!, activity!!.getString(R.string.info_name), nameChangeSubject::onNext)
    }

    private val nameChangeSubject: Subject<String> = PublishSubject.create()
    private val confirmDeleteSubject: Subject<Unit> = PublishSubject.create()

    init {
        appComponent
            .conversationInfoBuilder()
            .conversationInfoModule(ConversationInfoModule(this))
            .build()
            .inject(this)
    }

    override fun onViewCreated() {
        activity?.let { activity ->
            SmallNativeBannerLoad(
                activity,
                binding.myTemplate,
                binding.shimmerViewContainer,
                MyAddPrefs(activity).admNativeId,
                colors.theme().theme
            )

            binding.recyclerView.adapter = adapter
            binding.recyclerView.addItemDecoration(GridSpacingItemDecoration(adapter, activity))
            binding.recyclerView.layoutManager = GridLayoutManager(activity, 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                        if (adapter.getItemViewType(position) == 2) 1 else 3
                }
            }
        }

        themedActivity?.theme
            ?.autoDisposable(scope())
            ?.subscribe { binding.recyclerView.scrapViews() }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.info_title)
        showBackButton(true)
    }

    override fun render(state: ConversationInfoState) {
        if (state.hasError) {
            activity?.finish()
            return
        }

        adapter.data = state.data
    }

    override fun recipientClicks(): Observable<Long> = adapter.recipientClicks
    override fun recipientLongClicks(): Observable<Long> = adapter.recipientLongClicks
    override fun themeClicks(): Observable<Long> = adapter.themeClicks
    override fun nameClicks(): Observable<*> = adapter.nameClicks
    override fun nameChanges(): Observable<String> = nameChangeSubject
    override fun notificationClicks(): Observable<*> = adapter.notificationClicks
    override fun archiveClicks(): Observable<*> = adapter.archiveClicks
    override fun blockClicks(): Observable<*> = adapter.blockClicks
    override fun deleteClicks(): Observable<*> = adapter.deleteClicks
    override fun confirmDelete(): Observable<*> = confirmDeleteSubject
    override fun mediaClicks(): Observable<Long> = adapter.mediaClicks

    override fun showNameDialog(name: String) = nameDialog.setText(name).show()

    override fun showThemePicker(recipientId: Long) {
        router.pushController(
            RouterTransaction.with(ThemePickerController(recipientId))
                .pushChangeHandler(QkChangeHandler())
                .popChangeHandler(QkChangeHandler())
        )
    }

    override fun showBlockingDialog(conversations: List<Long>, block: Boolean) {
        blockingDialog.show(activity!!, conversations, block)
    }

    override fun requestDefaultSms() {
        navigator.showDefaultSmsDialog(activity!!)
    }

    override fun showDeleteDialog() {
        MaterialAlertDialogBuilder(activity!!)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(resources?.getQuantityString(R.plurals.dialog_delete_message, 1))
            .setPositiveButton(R.string.button_delete) { _, _ -> confirmDeleteSubject.onNext(Unit) }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

}