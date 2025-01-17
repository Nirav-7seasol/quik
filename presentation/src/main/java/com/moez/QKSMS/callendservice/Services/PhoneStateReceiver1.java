package com.moez.QKSMS.callendservice.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.moez.QKSMS.callendservice.MainCallActivity;
import com.moez.QKSMS.callendservice.utils.PreferencesManager;
import com.moez.QKSMS.common.SharedPrefs;
import com.moez.QKSMS.myadsworld.MyAllAdCommonClass;

import java.util.Date;

import dev.octoshrimpy.quik.common.App;

public class PhoneStateReceiver1 extends BroadcastReceiver {
    public static boolean isAdsConfig = false;
    static boolean isIncomingCall = false;
    public static boolean isIncomingCallEventSend = false;
    static boolean isMissCall = false;
    static boolean isOutgoingCall = false;
    static boolean isRinging = false;
    static boolean isShowScreen = false;
    static String outgoingSavedNumber;
    private Date callStartTime = new Date();
    PreferencesManager preferencesManager;
    protected Context savedContext;

    private void sendToMixpanel(String str) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.preferencesManager = PreferencesManager.getInstance(context);
        this.savedContext = context;
        if (intent == null || !"android.intent.action.PHONE_STATE".equals(intent.getAction())) {
            return;
        }
        try {
            Log.i("TAG-Bundle", "onReceive: " + new Gson().toJson(intent.getExtras()));
            Log.i("PhoneStateReceiver1", "onReceive: " + isAdsConfig);
            if (!isAdsConfig) {
                if (App.Companion.isConnected(context)) {
                    App.Companion.getCtx().getData(context);
                }
                isAdsConfig = true;
            }
            String stringExtra = intent.getStringExtra("state");
            Log.e("PhoneStateReceiver1", "ON_RECEIVE >>> " + stringExtra + " IS_NOT_SHOW_SCREEN >>> " + isShowScreen + " "
                    + SharedPrefs.Companion.isShowCalledId());
            if (Settings.canDrawOverlays(context) && /*QKApplication.Companion.getStoreBooleanValue("isSHowCallerID") &&*/ MyAllAdCommonClass.loadednative == null) {
//                AdsCachingUtils.preLoadBannerCdoAds(context, new MyAddPrefs(context).getAdmBannerId());
                MyAllAdCommonClass.startNativeLoad(context);
            }
            String str = outgoingSavedNumber;
            if (str == null || str.isEmpty()) {
                outgoingSavedNumber = intent.getStringExtra("incoming_number");
            }
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(stringExtra)) {
                if (!isIncomingCall && isRinging) {
                    isMissCall = true;
                    isIncomingCall = false;
                    isOutgoingCall = false;
                }
                String str2 = isIncomingCall ? "Incoming" : isMissCall ? "Missed Call" : "Outgoing";
                if (!isShowScreen/* && QKApplication.Companion.getStoreBooleanValue("isSHowCallerID")*/) {
                    sendToMixpanel("Call_End");
                    openNewActivity(context, outgoingSavedNumber, this.callStartTime, new Date(), str2);
                    isShowScreen = true;
                    return;
                }
                outgoingSavedNumber = null;
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stringExtra)) {
                this.callStartTime = new Date();
                if (!isRinging) {
                    isOutgoingCall = true;
                } else {
                    isIncomingCallEventSend = true;
                    sendToMixpanel("Incoming_Call");
                    isIncomingCall = true;
                }
                isShowScreen = false;
            } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(stringExtra)) {
                this.callStartTime = new Date();
                isRinging = true;
                isShowScreen = false;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void openNewActivity(Context context, String str, Date date, Date date2, String str2) {
        try {
            Log.i("phoneStateCheck", "openNewActivity: ");
            if (isIncomingCallEventSend) {
                sendToMixpanel("Request_to_native_ad_load");
            }
            Intent intent = new Intent(context, MainCallActivity.class);
            intent.putExtra("mobile_number", str);
            intent.putExtra("StartTime", date.getTime());
            intent.putExtra("EndTime", date2.getTime());
            intent.putExtra("CallType", str2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            outgoingSavedNumber = null;
            isMissCall = false;
            isIncomingCall = false;
            isOutgoingCall = false;
            isAdsConfig = false;
        } catch (Exception e) {
            Log.i("phoneStateCheck", "openNewActivity: Exception " + e.getMessage());
            e.printStackTrace();
        }
    }
}
