package com.moez.QKSMS.myadsworld;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.moez.QKSMS.feature.language.LanguageSelectionActivity;
import com.moez.QKSMS.common.SharedPrefs;
import com.moez.QKSMS.feature.permission.PermissionActivity;

import dev.octoshrimpy.quik.common.App;
import dev.octoshrimpy.quik.feature.main.MainActivity;

public class MySplashAppOpenAds {
    private static int counter = 0;
    static boolean loaded = false;

    public static void SplashAppOpenShow(Activity SplashActivity) {
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

                                goToNextStep(SplashActivity);
                            }
                        });
                    } else if (!loaded) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goToNextStep(SplashActivity);
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
                    goToNextStep(SplashActivity);
                }
            }, 500);
        }
    }


    static void goToNextStep(Activity activity) {
//        if (SharedPrefs.Companion.isInitialLanguageSet()) {
//            Intent intent = new Intent(activity, MainActivity.class);
//            activity.startActivity(intent);
//            activity.finish();
//        } else {
//            Intent intent = new Intent(activity, LanguageSelectionActivity.class);
//            activity.startActivity(intent);
//            activity.finish();
//        }
        if (SharedPrefs.Companion.isInitialLanguageSet()) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(activity, PermissionActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        } else {
            Intent intent = new Intent(activity, LanguageSelectionActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
