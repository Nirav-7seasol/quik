package com.moez.QKSMS.feature.permission

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.moez.QKSMS.callendservice.MyPermissionCenter
import com.moez.QKSMS.common.SharedPrefs
import com.moez.QKSMS.myadsworld.MyAllAdCommonClass
import dagger.android.AndroidInjection
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.common.base.QkThemedActivity
import dev.octoshrimpy.quik.common.util.extensions.viewBinding
import dev.octoshrimpy.quik.databinding.ActivityPermissionBinding
import dev.octoshrimpy.quik.feature.main.MainActivity
import java.util.Timer
import java.util.TimerTask

class PermissionActivity : QkThemedActivity() {

    private val binding by viewBinding(ActivityPermissionBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        shineAnimation()

        mPermissionForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Handle the result of the activity here
            }

        binding.btnGrant.setOnClickListener {
            val myPermissionCenter = MyPermissionCenter()
            myPermissionCenter.reqPermissions(
                this
            ) {
                MyAllAdCommonClass.isAppOpenshowornot = true
                askOverlayPermission()
            }
        }

    }

    private fun shineAnimation() {
        // attach the animation layout Using AnimationUtils.loadAnimation
        val anim = AnimationUtils.loadAnimation(this, R.anim.left_right)
        binding.shine.startAnimation(anim)
        // override three function There will error
        // line below the object
        // click on it and override three functions
        anim.setAnimationListener(object : Animation.AnimationListener {
            // This function starts the
            // animation again after it ends
            override fun onAnimationEnd(p0: Animation?) {
                binding.shine.startAnimation(anim)
            }

            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationRepeat(p0: Animation?) {}

        })
    }

    private var mPermissionForResult: ActivityResultLauncher<Intent>? = null

    private fun askOverlayPermission() {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        if (appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                Process.myUid(),
                packageName
            ) == AppOpsManager.MODE_ALLOWED
        ) {
            SharedPrefs.isShowCalledId = true
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            return
        }

        appOpsManager.startWatchingMode(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
            applicationContext.packageName,
            object : AppOpsManager.OnOpChangedListener {
                override fun onOpChanged(op: String?, packageName: String?) {
                    if (appOpsManager.checkOpNoThrow(
                            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                            Process.myUid(),
                            this@PermissionActivity.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                    ) {
                        return
                    }
                    appOpsManager.stopWatchingMode(this)

                    SharedPrefs.isShowCalledId = true

                    startActivity(Intent(this@PermissionActivity, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            })

        try {
            mPermissionForResult?.launch(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            )
        } catch (e: Exception) {
            Log.d("TAG", "askOverlayPermission: ${e.message}")
            e.printStackTrace()
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(
                    Intent(
                        this@PermissionActivity,
                        MyTranslucentActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("autostart", getString(R.string.allow_overlay_access))
                    })
            }
        }, 150L)
    }


}