package com.messages.readmms.readsmss.feature.main

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.facebook.shimmer.Shimmer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.ump.FormError
import com.jakewharton.rxbinding2.view.clicks
import com.messages.readmms.readsmss.BuildConfig
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.App
import com.messages.readmms.readsmss.common.Navigator
import com.messages.readmms.readsmss.common.androidxcompat.drawerOpen
import com.messages.readmms.readsmss.common.base.QkThemedActivity
import com.messages.readmms.readsmss.common.hide
import com.messages.readmms.readsmss.common.show
import com.messages.readmms.readsmss.common.util.extensions.autoScrollToStart
import com.messages.readmms.readsmss.common.util.extensions.dismissKeyboard
import com.messages.readmms.readsmss.common.util.extensions.resolveThemeColor
import com.messages.readmms.readsmss.common.util.extensions.scrapViews
import com.messages.readmms.readsmss.common.util.extensions.setBackgroundTint
import com.messages.readmms.readsmss.common.util.extensions.setTint
import com.messages.readmms.readsmss.common.util.extensions.setVisible
import com.messages.readmms.readsmss.common.util.extensions.viewBinding
import com.messages.readmms.readsmss.databinding.MainActivityBinding
import com.messages.readmms.readsmss.feature.blocking.BlockingDialog
import com.messages.readmms.readsmss.feature.changelog.ChangelogDialog
import com.messages.readmms.readsmss.feature.conversations.ConversationItemTouchCallback
import com.messages.readmms.readsmss.feature.conversations.ConversationsAdapter
import com.messages.readmms.readsmss.manager.ChangelogManager
import com.messages.readmms.readsmss.myadsworld.MyAddPrefs
import com.messages.readmms.readsmss.myadsworld.MyAllAdCommonClass
import com.messages.readmms.readsmss.myadsworld.MyAppOpenManager
import com.messages.readmms.readsmss.myadsworld.MyGoogleMobileAdsConsentManager
import com.messages.readmms.readsmss.repository.SyncRepository
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class MainActivity : QkThemedActivity(), MainView {

    @Inject
    lateinit var blockingDialog: BlockingDialog

    @Inject
    lateinit var disposables: CompositeDisposable

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var conversationsAdapter: ConversationsAdapter

    @Inject
    lateinit var drawerBadgesExperiment: DrawerBadgesExperiment

    @Inject
    lateinit var searchAdapter: SearchAdapter

    @Inject
    lateinit var itemTouchCallback: ConversationItemTouchCallback

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val onNewIntentIntent: Subject<Intent> = PublishSubject.create()
    override val activityResumedIntent: Subject<Boolean> = PublishSubject.create()
    override val queryChangedIntent: Subject<String> = PublishSubject.create()

    override val composeIntent by lazy { binding.compose.clicks() }
    override val drawerOpenIntent: Observable<Boolean> by lazy {
        binding.drawerLayout
            .drawerOpen(Gravity.START)
            .doOnNext { dismissKeyboard() }
    }
    override val homeIntent: Subject<Unit> = PublishSubject.create()
    override val navigationIntent: Observable<NavItem> by lazy {
        Observable.merge(
            listOf(
                backPressedSubject,
                binding.drawer.inbox.clicks().map { NavItem.INBOX },
                binding.drawer.archived.clicks().map { NavItem.ARCHIVED },
                binding.drawer.backup.clicks().map { NavItem.BACKUP },
                binding.drawer.scheduled.clicks().map { NavItem.SCHEDULED },
                binding.drawer.blocking.clicks().map { NavItem.BLOCKING },
                binding.drawer.settings.clicks().map { NavItem.SETTINGS },
                binding.drawer.help.clicks().map { NavItem.HELP },
                binding.drawer.invite.clicks().map { NavItem.INVITE },
                binding.drawer.language.clicks().map { NavItem.LANGUAGE })
        )
    }
    override val optionsItemIntent: Subject<Int> = PublishSubject.create()

    override val rateIntent by lazy { binding.drawer.rateOkay.clicks() }
    override val conversationsSelectedIntent by lazy { conversationsAdapter.selectionChanges }
    override val confirmDeleteIntent: Subject<List<Long>> = PublishSubject.create()
    override val swipeConversationIntent by lazy { itemTouchCallback.swipes }
    override val changelogMoreIntent by lazy { changelogDialog.moreClicks }
    override val undoArchiveIntent: Subject<Unit> = PublishSubject.create()
    override val snackbarButtonIntent by lazy { binding.snackbar.button.clicks() }

    private val binding by viewBinding(MainActivityBinding::inflate)
    private val viewModel by lazy {
        ViewModelProviders.of(
            this,
            viewModelFactory
        )[MainViewModel::class.java]
    }
    private val toggle by lazy {
        ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.main_drawer_open_cd,
            0
        )
    }
    private val itemTouchHelper by lazy { ItemTouchHelper(itemTouchCallback) }
    private val progressAnimator by lazy {
        ObjectAnimator.ofInt(
            binding.syncing.progress,
            "progress",
            0,
            0
        )
    }
    private val changelogDialog by lazy { ChangelogDialog(this) }
    private val snackbar by lazy { findViewById<View>(R.id.snackbar) }
    private val syncing by lazy { findViewById<View>(R.id.syncing) }
    private val lottie by lazy { findViewById<ImageView>(R.id.empty_list) }
    private val backPressedSubject: Subject<NavItem> = PublishSubject.create()

    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.bindView(this)
        onNewIntentIntent.onNext(intent)
        toggle.syncState()
        binding.toolbar.setNavigationOnClickListener {
            dismissKeyboard()
            homeIntent.onNext(Unit)
        }
        itemTouchCallback.adapter = conversationsAdapter
        conversationsAdapter.autoScrollToStart(binding.recyclerView)

        // Don't allow clicks to pass through the drawer layout
        binding.drawer.root.clicks().autoDisposable(scope()).subscribe()

        // Set the theme color tint to the recyclerView, progressbar, and FAB
        theme
            .autoDisposable(scope())
            .subscribe { theme ->
                // Set the color for the drawer icons
                val states = arrayOf(
                    intArrayOf(android.R.attr.state_activated),
                    intArrayOf(-android.R.attr.state_activated)
                )

                resolveThemeColor(R.attr.iconTintSolid)
                    .let { iconTintSolid ->
                        ColorStateList(
                            states,
                            intArrayOf(theme.theme, iconTintSolid)
                        )
                    }
                    .let { tintList ->
                        binding.drawer.inboxIcon.imageTintList = tintList
                        binding.drawer.archivedIcon.imageTintList = tintList
                    }

                binding.syncing.progress.progressTintList = ColorStateList.valueOf(theme.theme)
                binding.syncing.progress.indeterminateTintList = ColorStateList.valueOf(theme.theme)

                // Set the FAB compose icon color
                binding.compose.setBackgroundTint(theme.theme)
                binding.compose.setTint(theme.textPrimary)
                binding.progressBar.indeterminateTintList = ColorStateList.valueOf(theme.theme)
            }
        binding.drawer.versionName.text =
            getString(R.string.version_prefix, BuildConfig.VERSION_NAME)

        initAds()
    }

    private fun resetViews() {
        val isPrimaryViewsEnabled = isPrimaryViewsEnabled()
        val item = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = item?.actionView as? SearchView
        searchView?.isEnabled = isPrimaryViewsEnabled
        if (isPrimaryViewsEnabled) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    private fun isPrimaryViewsEnabled() =
        binding.snackbar.root.isVisible.not() && binding.syncing.root.isVisible.not()

    private fun initAds() {
        MyAppOpenManager.appOpenAd = null

        val myGoogleMobileAdsConsentManager =
            MyGoogleMobileAdsConsentManager.getInstance(applicationContext)
        myGoogleMobileAdsConsentManager?.gatherConsent(
            this
        ) { consentError: FormError? ->
            if (consentError != null) {
                // Consent not obtained in current session.
                Log.w(
                    "GoogleConsentError",
                    String.format(
                        "%s: %s",
                        consentError.errorCode,
                        consentError.message
                    )
                )
            }
            //                        Log.e("fgfgfffgffg", "onCreate: "+AESUTIL.decrypt(AllAdCommonClass.JSON_URL) );
            if (myGoogleMobileAdsConsentManager?.canRequestAds() == true) {
                initializeMobileAdsSdk()
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (myGoogleMobileAdsConsentManager?.canRequestAds() == true) {
            initializeMobileAdsSdk()
        }

        loadBanner()

        binding.drawerLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.drawerLayout.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.drawerLayout.rootView.height
            val keypadHeight = screenHeight - r.bottom

            // Check if the keyboard is open
            if (keypadHeight > screenHeight * 0.15) { // Keyboard is open if it covers 15% of the screen
                binding.cardAds.hide()
            } else {
                binding.cardAds.show()
            }
        }
        if (intent.getBooleanExtra("showInter", false)) {
            MyAllAdCommonClass.AdShowdialogFirstActivityQue(this) {}
        }
    }

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(
            this
        ) { // Load an ad.
            //                        loadAd();
            App.ctx.loadAppOpen()
        }
    }

    private fun loadBanner() {
        val shimmer = Shimmer.AlphaHighlightBuilder() // The builder for a ShimmerDrawable
            .setDuration(1800) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.9f) // the alpha of the underlying children
            .setHighlightAlpha(0.8f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

//        navigator.showDefaultSmsDialog(this)
        binding.bannerShimmer.setShimmer(shimmer)
        adView = AdView(this)
        adView?.adUnitId = (MyAddPrefs(this).admBannerId)
        binding.framBanner.addView(adView)
        val adSize: AdSize = getAdSize()
        adView?.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
        adView?.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                binding.bannerShimmer.visibility = GONE
                Log.d("TAG", "onAdFailedToLoad: " + loadAdError.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("TAG", "onAdFailedToLoad1: " + "loaded")
                binding.bannerShimmer.visibility = GONE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.run(onNewIntentIntent::onNext)
    }

    override fun render(state: MainState) {
        if (state.hasError) {
            finish()
            return
        }

        val addContact = when (state.page) {
            is Inbox -> state.page.addContact
            is Archived -> state.page.addContact
            else -> false
        }

        val markPinned = when (state.page) {
            is Inbox -> state.page.markPinned
            is Archived -> state.page.markPinned
            else -> true
        }

        val markRead = when (state.page) {
            is Inbox -> state.page.markRead
            is Archived -> state.page.markRead
            else -> true
        }

        val selectedConversations = when (state.page) {
            is Inbox -> state.page.selected
            is Archived -> state.page.selected
            else -> 0
        }

        binding.toolbar.menu.findItem(R.id.archive)?.isVisible =
            state.page is Inbox && selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.unarchive)?.isVisible =
            state.page is Archived && selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.delete)?.isVisible = selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.add)?.isVisible =
            addContact && selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.pin)?.isVisible =
            markPinned && selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.unpin)?.isVisible =
            !markPinned && selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.read)?.isVisible = markRead && selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.unread)?.isVisible =
            !markRead && selectedConversations != 0
        binding.toolbar.menu.findItem(R.id.block)?.isVisible = selectedConversations != 0

        binding.compose.setVisible((state.page is Inbox || state.page is Archived) && state.syncing !is SyncRepository.SyncProgress.Running)
        conversationsAdapter.emptyView =
            binding.empty.takeIf { state.page is Inbox || state.page is Archived }
        searchAdapter.emptyView = binding.empty.takeIf { state.page is Searching }

        when (state.page) {
            is Inbox -> {
                showBackButton(state.page.selected > 0)
                title = if (state.page.selected == 0) {
                    getString(R.string.app_name)
                } else {
                    getString(R.string.main_title_selected, state.page.selected)
                }
                if (binding.recyclerView.adapter !== conversationsAdapter) binding.recyclerView.adapter =
                    conversationsAdapter
                conversationsAdapter.updateData(state.page.data)
                itemTouchHelper.attachToRecyclerView(binding.recyclerView)
                lottie.hide()
                binding.empty.setText(R.string.inbox_empty_text)
            }

            is Searching -> {
                if (state.page.loading) {
                    binding.loadingLayout.show()
                } else {
                    binding.loadingLayout.hide()
                    showBackButton(true)
                    if (binding.recyclerView.adapter !== searchAdapter) binding.recyclerView.adapter =
                        searchAdapter
                    searchAdapter.data = state.page.data ?: listOf()
                    itemTouchHelper.attachToRecyclerView(null)
                    lottie.isVisible = searchAdapter.itemCount == 0
                    binding.empty.setText(R.string.inbox_search_empty_text)
                }
            }

            is Archived -> {
                showBackButton(state.page.selected > 0)
                title = when (state.page.selected != 0) {
                    true -> getString(R.string.main_title_selected, state.page.selected)
                    false -> getString(R.string.title_archived)
                }
                if (binding.recyclerView.adapter !== conversationsAdapter) binding.recyclerView.adapter =
                    conversationsAdapter
                conversationsAdapter.updateData(state.page.data)
                itemTouchHelper.attachToRecyclerView(null)
                lottie.hide()
                binding.empty.setText(R.string.archived_empty_text)
            }

            else -> {}
        }

        binding.drawer.inbox.isActivated = state.page is Inbox
        binding.drawer.archived.isActivated = state.page is Archived

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START) && !state.drawerOpen) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if (!binding.drawerLayout.isDrawerVisible(GravityCompat.START) && state.drawerOpen) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        when (state.syncing) {
            is SyncRepository.SyncProgress.Idle -> {
                syncing.isVisible = false
                snackbar.isVisible =
                    !state.defaultSms || !state.smsPermission || !state.contactPermission
            }

            is SyncRepository.SyncProgress.Running -> {
                syncing.isVisible = true
                binding.syncing.progress.max = state.syncing.max
                progressAnimator.apply {
                    setIntValues(
                        binding.syncing.progress.progress,
                        state.syncing.progress
                    )
                }.start()
                binding.syncing.progress.isIndeterminate = state.syncing.indeterminate
                snackbar.isVisible = false
            }
        }

        when {
            !state.defaultSms -> {
                binding.snackbar.title.setText(R.string.main_default_sms_title)
                binding.snackbar.message.setText(R.string.main_default_sms_message)
                binding.snackbar.button.setText(R.string.main_default_sms_change)
            }

            !state.smsPermission -> {
                binding.snackbar.title.setText(R.string.main_permission_required)
                binding.snackbar.message.setText(R.string.main_permission_sms)
                binding.snackbar.button.setText(R.string.main_permission_allow)
            }

            !state.contactPermission -> {
                binding.snackbar.title.setText(R.string.main_permission_required)
                binding.snackbar.message.setText(R.string.main_permission_contacts)
                binding.snackbar.button.setText(R.string.main_permission_allow)
            }
        }

        resetViews()
        val shouldSearchVisible = selectedConversations == 0 && isPrimaryViewsEnabled()
        binding.toolbar.menu.findItem(R.id.action_search)?.isVisible = shouldSearchVisible
        if (shouldSearchVisible.not()) {
            binding.toolbar.menu.findItem(R.id.action_search)?.collapseActionView()
        }
    }

    override fun onResume() {
        super.onResume()
        activityResumedIntent.onNext(true)
        adView?.resume()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            @Override
            fun run() {
                MyAllAdCommonClass.isAppOpenshowornot = false;
            }
        }, 1000L)
    }

    override fun onPause() {
        super.onPause()
        activityResumedIntent.onNext(false)
        adView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        adView?.destroy()
    }

    private fun getAdSize(): AdSize {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

    private var mAppUpdateManager: AppUpdateManager? = null
    private val RC_APP_UPDATE = 11

    var installStateUpdatedListener: InstallStateUpdatedListener =
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                    popupSnackbarForCompleteUpdate()
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (mAppUpdateManager != null) {
                        mAppUpdateManager!!.unregisterListener(this)
                    }
                } else {
                    Log.i(
                        "updateee", "InstallStateUpdatedListener: state: " + state.installStatus()
                    )
                }
            }
        }

    override fun onStart() {
        super.onStart()
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        mAppUpdateManager?.registerListener(installStateUpdatedListener)
        mAppUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/
                )
            ) {
                try {
                    mAppUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/,
                        this@MainActivity,
                        RC_APP_UPDATE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate()
            } else {
                Log.e("updateee", "checkForAppUpdateAvailability: something else")
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            binding.snackToast, "New app is ready!", Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") { view: View? ->
            if (mAppUpdateManager != null) {
                mAppUpdateManager!!.completeUpdate()
            }
        }
        snackbar.anchorView = binding.cardAds
        snackbar.setActionTextColor(resources.getColor(R.color.tools_theme))
        snackbar.show()
    }

    override fun showBackButton(show: Boolean) {
        toggle.onDrawerSlide(binding.drawer.root, if (show) 1f else 0f)
        toggle.drawerArrowDrawable.color = resolveThemeColor(android.R.attr.textColorPrimary)
    }

    override fun requestDefaultSms() {
        navigator.showDefaultSmsDialog(this)
    }

    override fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS
            ), 0
        )
    }

    override fun clearSearch() {
        dismissKeyboard()
        val item = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView
        searchView.setQuery("", false)
        searchView.onActionViewCollapsed()
        searchView.clearFocus()
    }

    override fun clearSelection() {
        conversationsAdapter.clearSelection()
    }

    override fun themeChanged() {
        binding.recyclerView.scrapViews()
    }

    override fun showBlockingDialog(conversations: List<Long>, block: Boolean) {
        blockingDialog.show(this, conversations, block)
    }

    override fun showDeleteDialog(conversations: List<Long>) {
        val count = conversations.size
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(resources.getQuantityString(R.plurals.dialog_delete_message, count, count))
            .setPositiveButton(R.string.button_delete) { _, _ ->
                confirmDeleteIntent.onNext(
                    conversations
                )
            }
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

    override fun showChangelog(changelog: ChangelogManager.CumulativeChangelog) {
        changelogDialog.show(changelog)
    }

    override fun showArchivedSnackbar() {
        Snackbar.make(binding.drawerLayout, R.string.toast_archived, Snackbar.LENGTH_LONG).apply {
            setAction(R.string.button_undo) { undoArchiveIntent.onNext(Unit) }
            setActionTextColor(colors.theme().theme)
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        searchView.setQuery("", false);
        searchView.setQueryHint("Search here...")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    queryChangedIntent.onNext(query)
                }
                return true
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        optionsItemIntent.onNext(item.itemId)
        return true
    }

    fun hideSoftKeyboard(activity: Activity, view: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    override fun onBackPressed() {
        backPressedSubject.onNext(NavItem.BACK)
    }

}
