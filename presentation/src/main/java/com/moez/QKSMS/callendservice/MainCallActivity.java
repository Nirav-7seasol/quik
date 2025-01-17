package com.moez.QKSMS.callendservice;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.moez.QKSMS.callendservice.adapter.CallerScreenAdapter;
import com.moez.QKSMS.callendservice.interfaces.OnKeyboardOpenListener;
import com.moez.QKSMS.callendservice.model.ContactCDO;
import com.moez.QKSMS.callendservice.utils.AppUtils;
import com.moez.QKSMS.callendservice.utils.CDOUtiler;
import com.moez.QKSMS.callendservice.utils.ConstantsKt;
import com.moez.QKSMS.callendservice.utils.Utils;
import com.moez.QKSMS.myadsworld.MyAddPrefs;
import com.moez.QKSMS.myadsworld.MyAllAdCommonClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dev.octoshrimpy.quik.R;
import dev.octoshrimpy.quik.common.App;
import dev.octoshrimpy.quik.databinding.ActivityMainCallBinding;

public final class MainCallActivity extends BaseActivity implements OnKeyboardOpenListener {
    private ActivityMainCallBinding binding;
    private ContactCDO contact1;
    private String number = "";
    private String contactName = "";
    private String contactId = "";
    private String time = "00:00";
    private String callStatus = "";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityMainCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadNativeOrBannerAds();

        setThemeData();
        init();
        UIComponent();

        ConstantsKt.setKeyboardVisibilityListener(this, new OnKeyboardOpenListener() {
            @Override
            public void onKeyBoardIsOpen(boolean z) {
                if (App.Companion.isConnected(MainCallActivity.this)) {
                    if (z) {
                        Log.i("MAIN::KEY", "OPEN :: " + z);
                        binding.cardBottom.setVisibility(View.GONE);
                        return;
                    }
                    Log.i("MAIN::KEY", "OPEN :: " + z);
                    binding.cardBottom.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private final void setThemeData() {
        try {
            if (Build.VERSION.SDK_INT != 26) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void setSecondPage() {
        binding.viewPager.setCurrentItem(1, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void init() {
        try {
            if (getIntent() != null) {
                long longExtra = getIntent().getLongExtra("StartTime", 0L);
                this.time = getTimeDiff(longExtra, getIntent().getLongExtra("EndTime", 0L));
                this.number = String.valueOf(getIntent().getStringExtra("mobile_number"));
                this.callStatus = String.valueOf(getIntent().getStringExtra("CallType"));

                Log.d("TAG", "initnumber: " + number);
                Log.d("TAG-Bundle", getIntent().getExtras().toString());
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
//        setHomeButtonPressListener();
    }

//    private final void setHomeButtonPressListener() {
//        try {
//            HomeWatcher homeWatcher = new HomeWatcher(this, this);
//            homeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
//                @Override
//                public void onHomePressed() {
//                    Log.e("HomeWatcher", "ON_HOME_PRESSED >>> CALLED >>> MAIN_CALL_SCREEN");
//                    finish();
//                }
//
//                @Override
//                public void onHomeLongPressed() {
//                    Log.e("HomeWatcher", "ON_HOME_LONG_PRESSED >>> CALLED >>> MAIN_CALL_SCREEN");
//                    finish();
//                }
//            });
//            homeWatcher.startWatch();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private final void setAdapterData() {
        CallerScreenAdapter callerScreenAdapter = new CallerScreenAdapter(this, this.number);
        callerScreenAdapter.setContactData(this.contactName, this.contactId, this.contact1);
        binding.viewPager.setAdapter(callerScreenAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public final void onConfigureTab(TabLayout.Tab tab, int i) {
                if (i == 0) {
                    tab.setIcon(R.drawable.ic_action_call_m);
                    tab.getIcon().setColorFilter(ContextCompat.getColor(MainCallActivity.this, R.color.commonwhite), PorterDuff.Mode.SRC_IN);
                } else if (i == 1) {
                    tab.setIcon(R.drawable.ic_action_msg_m);
                    tab.getIcon().setColorFilter(ContextCompat.getColor(MainCallActivity.this, R.color.commonwhite), PorterDuff.Mode.SRC_IN);
                } else if (i == 2) {
                    tab.setIcon(R.drawable.ic_action_notifi_m);
                    tab.getIcon().setColorFilter(ContextCompat.getColor(MainCallActivity.this, R.color.commonwhite), PorterDuff.Mode.SRC_IN);
                } else {
                    tab.setIcon(R.drawable.ic_action_block_m);
                    tab.getIcon().setColorFilter(ContextCompat.getColor(MainCallActivity.this, R.color.commonwhite), PorterDuff.Mode.SRC_IN);
                }
                ;
            }
        }).attach();
    }

    private final void UIComponent() {

        if (!AppUtils.isEmptyString(this.number)) {
            if (Utils.getContact(this, this.number) == null) {
                binding.txtAppName.setText(PhoneNumberUtils.formatNumber(this.number, "IN"));
                binding.ImageView.setVisibility(View.VISIBLE);
                binding.txtUserProName.setVisibility(View.GONE);
                binding.callerAvatar.setVisibility(View.GONE);
                binding.itemTvContactFirstLetter.setVisibility(View.GONE);
            } else {
                ContactCDO contact = Utils.getContact(this, this.number);
                this.contact1 = contact;
                this.contactName = contact.getNameSuffix();
                this.contactId = String.valueOf(contact.getContactId());
                binding.txtAppName.setText(contact.getNameSuffix());
                binding.ImageView.setVisibility(View.GONE);
                binding.itemTvContactFirstLetter.setVisibility(View.GONE);
                binding.callerAvatar.setVisibility(View.GONE);
                String contactPhotoUri = contact.getContactPhotoUri();
                boolean z = true;
                if (!(contactPhotoUri == null || contactPhotoUri.isEmpty())) {
                    binding.callerAvatar.setVisibility(View.VISIBLE);
                    binding.txtUserProName.setVisibility(View.GONE);
                    RequestBuilder<Drawable> load = Glide.with((FragmentActivity) this).load(contact.getContactPhotoUri());
                    load.into(binding.callerAvatar);
                } else {
                    String contactPhotoThumbUri = contact.getContactPhotoThumbUri();
                    if (contactPhotoThumbUri != null && contactPhotoThumbUri.length() != 0) {
                        z = false;
                    }
                    if (!z) {
                        binding.callerAvatar.setVisibility(View.VISIBLE);
                        binding.txtUserProName.setVisibility(View.GONE);
                        RequestBuilder<Drawable> load2 = Glide.with((FragmentActivity) this).load(contact.getContactPhotoThumbUri());
                        load2.into(binding.callerAvatar);
                    } else {
                        binding.txtUserProName.setVisibility(View.VISIBLE);
                        binding.callerAvatar.setVisibility(View.GONE);
                        binding.txtUserProName.setText(Utils.firstStringer(contact.getNameSuffix()));
                    }
                }
            }
        }

        setAdapterData();

        binding.txtCalliInfo.setText("" + this.time);
        binding.txtCallStatus.setText(this.callStatus);
        Date time = Calendar.getInstance().getTime();
        binding.txtTime.setText(new SimpleDateFormat("hh:mm").format(time).toString());
        binding.txtCalliInfo.setText("" + this.time);

        binding.imgAppIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                try {
                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    if (launchIntentForPackage != null) {
                        if (!AppUtils.isAppRunning) {
                            startActivity(launchIntentForPackage);
                            finish();
                        } else {
                            finish();
                            CDOUtiler.isClickAppIcon = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.imgCalliCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                Utils.openDialerPad(MainCallActivity.this, number);
                finishAndRemoveTask();
            }
        });
    }

    private final void loadNativeOrBannerAds() {
        CDOUtiler.initializeAllAdsConfigs(this);

        try {
            if (MyAllAdCommonClass.loadednative != null) {
                Log.e("package:mine FullScreenBannerAds", "loadNativeOrBannerAds: isCDOBannerAvailable showFullScreenBannerAds");
                binding.myTemplate.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.myTemplate.setNativeAd(MyAllAdCommonClass.loadednative, new MyAddPrefs(MainCallActivity.this).getButtonColor());
                MyAllAdCommonClass.isnativeload = true;
                return;
            }
            binding.shimmerViewContainer.startShimmer();
            MyAllAdCommonClass.SmallNativeBannerLoad(this, binding.myTemplate, binding.shimmerViewContainer,
                    new MyAddPrefs(this).getAdmCallEndNativeId(),
                    ContextCompat.getColor(MainCallActivity.this, R.color.tools_theme));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private final String getTimeDiff(long j, long j2) {
        long j4 = j2 - j;
        long j5 = 60;
        long j6 = (j4 / 1000) % j5;
        long j7 = (j4 / 60000) % j5;
        if ((j4 / 3600000) % 24 > 0) {
            return AppUtils.addExtraZero(j5) + ':' + AppUtils.addExtraZero(j7) + ':' + AppUtils.addExtraZero(j6);
        }
        return AppUtils.addExtraZero(j7) + ':' + AppUtils.addExtraZero(j6);
    }

    @Override
    protected void onDestroy() {
        Log.i("FullScreenBannerAds", "onDestroy: " + MyAllAdCommonClass.isnativeload);
        if (MyAllAdCommonClass.loadednative != null) {
            MyAllAdCommonClass.loadednative = null;
        }
        MyAllAdCommonClass.isnativeload = true;

        super.onDestroy();
    }

    @Override
    public void onKeyBoardIsOpen(boolean z) {
        if (z) {
            Log.i("MAIN::KEY", "OPEN :: " + z);
            binding.cardBottom.setVisibility(View.GONE);
            return;
        }
        Log.i("MAIN::KEY", "OPEN :: " + z);
        binding.cardBottom.setVisibility(View.VISIBLE);
    }
}
