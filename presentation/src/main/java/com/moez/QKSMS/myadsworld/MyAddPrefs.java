package com.moez.QKSMS.myadsworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyAddPrefs {
    public static SharedPreferences prefs;

    public String isFirstTime = "isFirstTime";
    public Editor editor;

    public MyAddPrefs(Context context) {
        prefs = context.getSharedPreferences("USER PREFS", Context.MODE_PRIVATE);
        editor = this.prefs.edit();
    }

    public boolean getisFirstTime() {
        return prefs.getBoolean(isFirstTime, true);
    }

    public void setisFirstTime(boolean str) {
        editor.putBoolean(isFirstTime, str).commit();
    }

    public boolean getisQuickFirstTime() {
        return prefs.getBoolean("QuickFirstTime", true);
    }

    public void setisQuickFirstTime(boolean str) {
        editor.putBoolean("QuickFirstTime", str).commit();
    }

    public boolean getRateDialogFirstTime() {
        return prefs.getBoolean("RateDialogFirstTime", true);
    }

    public void setRateDialogFirstTime(boolean str) {
        editor.putBoolean("RateDialogFirstTime", str).commit();
    }

    public boolean getisLanguageSet() {
        return prefs.getBoolean("isLanguageSet", false);
    }

    public void setisLanguageSet(boolean str) {
        editor.putBoolean("isLanguageSet", str).commit();
    }

    public boolean getAdsPersonalization() {
        return prefs.getBoolean("AdsPersonalization", false);
    }

    public void setAdsPersonalization(boolean b) {
        editor.putBoolean("AdsPersonalization", b).commit();
    }

    public void setAdmAppOpenId(String sid) {
        editor.putString("admappid", sid).commit();
    }

    public String getAdmAppOpenId() {
        return prefs.getString("admappid", "ca");
    }

    public void setAdmBannerId(String sid) {
        editor.putString("admbannerid", sid).commit();
    }

    public String getAdmBannerId() {
        return prefs.getString("admbannerid", "ca");
    }

    public void setAdmInterId(String sid) {
        editor.putString("adminterid", sid).commit();
    }

    public String getAdmInterId() {
        return prefs.getString("adminterid", "ca");
    }

    public void setAdmNativeId(String sid) {
        editor.putString("admnativeid", sid).commit();
    }

    public String getAdmNativeId() {
        return prefs.getString("admnativeid", "ca");
    }

    public String getAdmCallEndNativeId() {
//        return "ca-app-pub-2524225827032053/3613276134";
        return "ca-app-pub-3940256099942544/2247696110";
    }

    public void setButtonColor(String sid) {
        editor.putString("addButtonColor", sid).commit();
    }

    public String getButtonColor() {
        return prefs.getString("addButtonColor", "#2F9E33");
    }

    public void set_drawer_select(int num) {
        editor.putInt("drawer_select", num).commit();
    }

    public int get_drawer_select() {
        return prefs.getInt("drawer_select", 1);
    }

}
