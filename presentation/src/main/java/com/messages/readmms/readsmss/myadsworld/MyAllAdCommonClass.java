package com.messages.readmms.readsmss.myadsworld;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import com.messages.readmms.readsmss.R;


public class MyAllAdCommonClass {

    public static final String JSON_URL = "https://7seasol-application.s3.amazonaws.com/admin_prod/comtesting.json";
//        public static final String JSON_URL = "https://7seasol-application.s3.amazonaws.com/admin_prod/pbz-zrffntrf-ernqzzf-ernqfzff.json";
//        public static final String JSON_URL = "7+dH3xwW79YajlGM74j5OKZ6Z6wBx7zUfM+f4xFUnE18qcgC4roZdtTR4a8tVb+s7s3lkdKmj830JclVAtLxoSOgvWLrQKutYqWUM8T3cjcglT23gX8qoOzmeFvOlzEz";
//    public static final String JSON_URL = "https://7seasol-application.s3.amazonaws.com/admin_prod/pbz-zrffntrf-fzf-grkgzrffntrf.json";
//    public static final String JSON_URL = "7+dH3xwW79YajlGM74j5OKZ6Z6wBx7zUfM+f4xFUnE18qcgC4roZdtTR4a8tVb+s7s3lkdKmj830JclVAtLxociJMvvQYP5xXMb4kZqGtOCKZU6yulWUoi+q1x/iRhN1";

    public static InterstitialAd mInterstitialAd;
    public static MyListener myListener;

    public static NativeAd loadednative;

    public static boolean isinterload = true;
    public static boolean isnativeload = true;

    public static AlertDialog showdialog;

    public static boolean isAppOpenshowornot = false;

    public interface MyListener {
        void callback();
    }

    public static boolean isActivityActive(Activity activity) {
        if (activity != null) {
            return (activity.isFinishing() || activity.isDestroyed()) ? false : true;
        }
        return false;
    }

    public static boolean isContextActive(Context context) {
        if (context != null) {
            if (context instanceof Activity) {
                return isActivityActive((Activity) context);
            }
            return true;
        }
        return false;
    }


    public static void load_Admob_Interstial(final Context context) {
        if (isinterload) {
            isinterload = false;
            if (mInterstitialAd == null) {

                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(context, new MyAddPrefs(context).getAdmInterId(), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                        mInterstitialAd = interstitialAd;
                        Log.e("Admobintertitial", "Admob Inter FirstonAdLoaded: ");

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        Log.e("Admobintertitial", "Admob Inter onAdFailedToLoad: " + loadAdError.getMessage());
                        isinterload = true;
                        mInterstitialAd = null;
//                    LoadFb_Interstial(context);

                    }

                });
            }
        }
    }

    public static void dialogProgress(Activity context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.exitdialog_style);

        View view = LayoutInflater.from(context).inflate(R.layout.showads_dialog, null);
        builder.setView(view);

        showdialog = builder.create();
        showdialog.show();

        showdialog.setCancelable(false);

    }

    public static void AdShowdialogFirstActivityQue(final Activity context, MyListener myListenerData) {
        myListener = myListenerData;

        if (mInterstitialAd != null) {
            mInterstitialAd.show(context);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    myListener.callback();
                    isAppOpenshowornot = false;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    myListener.callback();
                    isAppOpenshowornot = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    isAppOpenshowornot = true;
                }
            });
        } else {

            dialogProgress(context);
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, new MyAddPrefs(context).getAdmInterId(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    Log.e("Admobintertitial", "Admob Inter FirstonAdLoaded: ");
                    interstitialAd.show((Activity) context);
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {

                            if (showdialog != null) {
                                showdialog.dismiss();
                            }
                            myListener.callback();
                            isAppOpenshowornot = false;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            if (showdialog != null) {
                                showdialog.dismiss();
                            }
                            myListener.callback();
                            isAppOpenshowornot = false;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isAppOpenshowornot = true;
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    if (showdialog != null) {
                        showdialog.dismiss();
                    }
                    myListener.callback();
                    isAppOpenshowornot = false;
                }
            });
        }
    }

    public static void SmallNativeBannerLoad(Context context, final TemplateView template, ShimmerFrameLayout shimmerFrameLayout, String nativeId) {

        AdLoader adLoader = new AdLoader.Builder(context, nativeId).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                Log.e("NativeFirstnativeload", "Firston NativeAdLoaded: ");
                template.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
                template.setNativeAd(nativeAd, new MyAddPrefs(context).getButtonColor());
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("NativeFirstnativeload", "onAdFailedToLoad: " + loadAdError.getMessage());
                isnativeload = false;
                shimmerFrameLayout.setVisibility(View.GONE);
//                load_Fb_Native_banner(context, template, rl, fblayout, txtadsloading, quizfram, shimmerFrameLayout);
            }
        }).build();


        adLoader.loadAd(new AdRequest.Builder().build());

    }

    public static void SmallNativeBannerLoad(Context context, final TemplateView template, ShimmerFrameLayout shimmerFrameLayout, String nativeId, int buttonColor) {

        AdLoader adLoader = new AdLoader.Builder(context, nativeId).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                Log.e("NativeFirstnativeload", "Firston NativeAdLoaded: ");
                template.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
                template.setNativeAd(nativeAd, buttonColor);
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("NativeFirstnativeload", "onAdFailedToLoad: " + loadAdError.getMessage());
                isnativeload = false;
                shimmerFrameLayout.setVisibility(View.GONE);
//                load_Fb_Native_banner(context, template, rl, fblayout, txtadsloading, quizfram, shimmerFrameLayout);
            }
        }).build();


        adLoader.loadAd(new AdRequest.Builder().build());

    }

    public static void RateApp(Activity mContext) {
        try {
            ReviewManager manager = ReviewManagerFactory.create(mContext);
            manager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                @Override
                public void onComplete(@NonNull Task<ReviewInfo> task) {
                    if (task.isSuccessful()) {
                        ReviewInfo reviewInfo = task.getResult();
                        manager.launchReviewFlow((Activity) mContext, reviewInfo).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                new MyAddPrefs(mContext).set_drawer_select(1);
                                String url = "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                mContext.startActivity(intent);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                try {
                                    new MyAddPrefs(mContext).set_drawer_select(1);
                                    if (!new MyAddPrefs(mContext).getRateDialogFirstTime()) {
                                        String url = "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        mContext.startActivity(intent);
                                    } else {
                                        new MyAddPrefs(mContext).setRateDialogFirstTime(false);
                                        mContext.finishAffinity();
                                    }
                                } catch (Exception e) {
                                    Log.e("TAG", "onComplete: ");
                                }

                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    new MyAddPrefs(mContext).set_drawer_select(1);
                    String url = "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(intent);
                }
            });
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startNativeLoad(Context context) {
        if (isnativeload) {
            isnativeload = false;
            new AdLoader.Builder(context, new MyAddPrefs(context).getAdmCallEndNativeId()).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            Log.e("Firstnativeload", "Firston PreNativeAdLoaded: ");
                            loadednative = nativeAd;
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            isnativeload = true;
                            loadednative = null;
                            Log.e("Firstnativeload", "PreonAdFailedToLoad: " + loadAdError.getMessage());
                        }
                    }).withNativeAdOptions(new NativeAdOptions.Builder()
                            .setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build()
                    .loadAd(new AdRequest.Builder().build());
        }
    }

}
