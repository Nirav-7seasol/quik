package com.messages.readmms.readsmss.myadsworld;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.messages.readmms.readsmss.common.App;
import com.messages.readmms.readsmss.common.SharedPrefs;

public class MySplashAppOpenAds {
    private static int counter = 0;
    static boolean loaded = false;

    public static void SplashAppOpenShow(Activity SplashActivity, Intent targetIntent) {
        MyAddPrefs appPreferences = new MyAddPrefs(SplashActivity);
        if (App.Companion.isConnected(SplashActivity) && !appPreferences.getAdmAppOpenId().isEmpty()) {

            new CountDownTimer(4000, 1000) {
                public void onTick(long millisUntilFinished) {
                    counter++;
                    Log.e("@@SplashBeta", " - " + MyAppOpenManager.isAdAvailable());

                    if (MyAppOpenManager.isFailappOpen()) {
                        cancel();
                        onFinish();
                    } else if (MyAppOpenManager.isAdAvailable()) {
                        loaded = true;
                        cancel();
                        onFinish();
                    }
                    Log.e("@@SplashBeta", "- " + counter);
                }

                public void onFinish() {
                    if (!MyAppOpenManager.isShowingAd && MyAppOpenManager.isAdAvailable()) {
                        MyAppOpenManager.showAdIfAvailableAds(SplashActivity, new onInterCloseCallBack() {
                            @Override
                            public void onAdsClose() {
                                SharedPrefs.Companion.setFirstAppOpenPending(false);
                                goToNextStep(SplashActivity, targetIntent);
                            }
                        });
                    } else if (!loaded) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goToNextStep(SplashActivity, targetIntent);
                            }
                        }, 500);
                    }
//                    else {
//                        goToNextStep(appPreferences, SplashActivity, StartActivity, ActivityFirstTime);
//                    }
                }
            }.start();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNextStep(SplashActivity, targetIntent);
                }
            }, 500);
        }
    }


    static void goToNextStep(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.finish();
//        if (!Settings.canDrawOverlays(activity)) {
//            Intent intent = new Intent(activity, PermissionActivity.class);
//            activity.startActivity(intent);
//            activity.finish();
//        } else {
//            Intent intent = new Intent(activity, MainActivity.class);
//            activity.startActivity(intent);
//            activity.finish();
//        }
    }
}
