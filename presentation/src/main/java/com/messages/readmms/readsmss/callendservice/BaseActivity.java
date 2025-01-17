package com.messages.readmms.readsmss.callendservice;

import android.os.Bundle;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.messages.readmms.readsmss.callendservice.utils.CDOUtiler;

public class BaseActivity extends LocalizationActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            CDOUtiler.hideNavigationBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            CDOUtiler.hideNavigationBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            try {
                CDOUtiler.hideNavigationBar(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
